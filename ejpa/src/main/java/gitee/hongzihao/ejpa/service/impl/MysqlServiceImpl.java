package gitee.hongzihao.ejpa.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import gitee.hongzihao.ejpa.module.entity.DistributedLock;
import gitee.hongzihao.ejpa.module.jpa.DistributedLockJpa;
import gitee.hongzihao.ejpa.service.MysqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MysqlServiceImpl implements MysqlService {

	private static ConcurrentHashMap<String,Boolean> checkFinishMap = new ConcurrentHashMap<>();

	@Autowired
	@Lazy
	private DistributedLockJpa distributedLockJpa;

	@Autowired
	@Lazy
	private MysqlService mysqlService;


	public AtomicBoolean isRun =new AtomicBoolean(false);

	@PersistenceContext
	@Lazy
	protected EntityManager manager;

	@Override
	@Transactional(rollbackFor = Exception.class,propagation =  Propagation.REQUIRES_NEW)
	public boolean lock(String key, int lockTime, int waitTime) {
		waitTime  =waitTime==0?1:waitTime;
		try {
			int loop = waitTime*1;
			for (int i = 0; i < loop; i++) {
				try {
					long size =lockBykey(key, lockTime);
					if(size>0){
						return true;
					}
					if(waitTime==1){
						return  false;
					}
					//如果设置的等待的时间就停止500毫秒
				}catch (Exception e){
					e.printStackTrace();
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation =  Propagation.REQUIRES_NEW)
	public boolean unLock(String key) {
		try {
			String delete ="DELETE FROM `distributed_lock` WHERE keyword ='"+key+"'";
			manager.createNativeQuery(delete).executeUpdate();
			return true;
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
//	@Transactional(rollbackFor = Exception.class,propagation =  Propagation.REQUIRES_NEW)
	public void deleteExpiredKey() {
		//多机情况下只能2台机器执行自动删除动作
		long key = System.currentTimeMillis() % 2;
		boolean isLock = false;
		String keyword = key + "masterMysqlDoRun";
		int lockTime = 10;
		try {
			isLock = mysqlService.lock(keyword, lockTime, 1);
			if (!isLock) {
				//加锁不成功再检查下，避免2台负责清除过期的锁的机器都下线了
				List<String> ids = (List<String>) distributedLockJpa.findByLockTimeBeforeAndKeyword(keyword,new Date());
				mysqlService.deleteIds(ids);
				return;
			}
			for (int i = 0; i < lockTime; i++) {
				List<String> ids = (List<String>) distributedLockJpa.findByLockTimeBefore(new Date());
				mysqlService.deleteIds(ids);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isLock) {
				mysqlService.unLock(keyword);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation =  Propagation.REQUIRES_NEW)
	public void deleteIds(List<String> ids) {
		if (ids == null || ids.size() <= 0) {
			return;
		}
		String idArray = "";
		for (String id : ids) {
			idArray = idArray + "'" + id + "',";
		}
		if (idArray.endsWith(",")) {
			idArray = idArray.substring(0, idArray.length() - 1);
		}
		String delete = "DELETE FROM `distributed_lock` WHERE id in (" + idArray + ")";
		manager.createNativeQuery(delete).executeUpdate();
	}

	private Long lockBykey(String key, int lockTime){
		try {
			long size = distributedLockJpa.countByKeyword(key);
			if(size>0){
				throw  new Exception("key已经被锁");
			}
			Date now = DateUtil.offsetSecond(new Date(),lockTime);
			DistributedLock lock = new DistributedLock();
			lock.setKeyword(key);
			lock.setLockTime(now);
			String id = UUID.randomUUID()+RandomUtil.randomString(2);
			String insertSql =  "INSERT INTO `distributed_lock` (`id`, `keyword`, `lock_time`, `gmt_create`, `gmt_modified`, `is_delete`) " +
					"VALUES ('"+id+"', '"+key+"', '"+DateUtil.formatDateTime(now)+"', now(),  now(), '0')";
			manager.createNativeQuery(insertSql).executeUpdate();
			//设置加到
			checkFinishMap.put(id,true);
			return  1L;
		}catch (Exception e){
			e.printStackTrace();
		}
		return  0L;
	}



}
