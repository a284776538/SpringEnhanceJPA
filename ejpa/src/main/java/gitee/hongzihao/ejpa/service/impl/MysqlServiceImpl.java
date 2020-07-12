package gitee.hongzihao.ejpa.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import gitee.hongzihao.ejpa.module.entity.DistributedLock;
import gitee.hongzihao.ejpa.module.jpa.DistributedLockJpa;
import gitee.hongzihao.ejpa.service.MysqlService;
import gitee.hongzihao.ejpa.util.ExtendLockThread;
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

	//保存当前分布式锁
	private static ConcurrentHashMap<String,Integer> lockMap = new ConcurrentHashMap<>();

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

	/**
	 *
	 * @param key 锁
	 * @param lockTime 锁时间 秒
	 * @param waitTime 加锁失败重试等待时间 秒
	 * @return true 加锁成功，false 失败
	 */
//	@Override
//	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
//	public boolean lock(String key, int lockTime, int waitTime) {
//		if (key == null) {
//			return false;
//		}
//		waitTime = waitTime == 0 ? 1 : waitTime;
//		try {
//			int loop = waitTime * 1;
//			for (int i = 0; i < loop; i++) {
//				try {
//					long size = lockBykey(key, lockTime,false);
//					if (size > 0) {
//						return true;
//					}
//					if (waitTime == 1) {
//						return false;
//					}
//					//如果设置的等待的时间就停止500毫秒
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				Thread.sleep(1000);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public boolean lock(String key, int lockTime, int waitTime, boolean isExtend) {
		if (key == null) {
			return false;
		}
		waitTime = waitTime == 0 ? 1 : waitTime;
		try {
			int loop = waitTime * 1;
			for (int i = 0; i < loop; i++) {
				try {
					long size = lockBykey(key, lockTime,isExtend);
					if (size > 0) {
						return true;
					}
					if (waitTime == 1) {
						return false;
					}
					//如果设置的等待的时间就停止500毫秒
				} catch (Exception e) {
					e.printStackTrace();
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 分布式锁解锁
	 * @param key 解锁的key
	 * @return
	 */
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
			isLock = mysqlService.lock(keyword, lockTime, 1,false);
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

	@Override
	@Transactional(rollbackFor = Exception.class,propagation =  Propagation.REQUIRES_NEW)
	public int extendTimes(String id, int lockTime) {
		String delete = "UPDATE  `distributed_lock` SET  `lock_time` =   now()+interval "+lockTime+" second WHERE id = '"+id+"'";
		int i = manager.createNativeQuery(delete).executeUpdate();
		return  i ;
	}

	private Long lockBykey(String key, int lockTime, boolean isExtend){
		try {
			long size = distributedLockJpa.countByKeyword(key);
			if(size>0){
				return  0L;
//				throw  new Exception("key已经被锁");
			}
			Date now = DateUtil.offsetSecond(new Date(),lockTime);
			DistributedLock lock = new DistributedLock();
			lock.setKeyword(key);
			lock.setLockTime(now);
			String id = UUID.randomUUID()+RandomUtil.randomString(2);
			String insertSql =  "INSERT INTO `distributed_lock` (`id`, `keyword`, `lock_time`, `gmt_create`, `gmt_modified`, `is_delete`) " +
					"VALUES ('"+id+"', '"+key+"', '"+DateUtil.formatDateTime(now)+"', now(),  now(), '0')";
			manager.createNativeQuery(insertSql).executeUpdate();
			//开启自动续期线程
			if(isExtend){
				new ExtendLockThread(id,lockTime,mysqlService).start();
			}
			return  1L;
		}catch (Exception e){
			e.printStackTrace();
		}
		return  0L;
	}






}
