package gitee.hongzihao.ejpa.service;

import java.util.List;

/**
 * Created by Charlie on 2019/5/12.
 */
public interface MysqlService {

	/**
	 * mysql加锁
	 * @param key
	 * @param lockTime 锁时间 秒
	 * @param waitTime 等待时间 秒 0不等待
	 * @return
	 */
//	boolean lock(String key, int lockTime, int waitTime);



	/**
	 * mysql加锁
	 * @param key
	 * @param lockTime 锁时间 秒
	 * @param waitTime 等待时间 秒 0不等待
	 * @param isExtend 是否自动延期
	 * @return
	 */
	boolean lock(String key, int lockTime, int waitTime,boolean isExtend);

	/**
	 * 解锁
	 * @param key
	 * @return
	 */
	boolean unLock(String key);



	/**
	 * 删除过期的关键字
	 * @return
	 */
	void deleteExpiredKey();

	/**
	 * 删除过期的关键字
	 * @return
	 */
	void deleteIds(List<String> ids);

	/**
	 * 删除过期的关键字
	 * @return
	 */
	int extendTimes(String id, int lockTime);

}
