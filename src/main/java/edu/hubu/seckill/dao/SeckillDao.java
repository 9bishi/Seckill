package edu.hubu.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import edu.hubu.seckill.entity.Seckill;

public interface SeckillDao {
	/**
	 * �����
	 * @param seckillId
	 * @param killTime
	 * @return ���Ӱ������>1,��ʾ���µļ�¼����
	 */
	int reduceNumber(@Param("seckillId")long seckillId,@Param("killTime")Date killTime);
	
	/**
	 * ����id��ѯ��ɱ����
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);
	/**
	 * ����ƫ���� ��ѯ����
	 * @param offest
	 * @param limit
	 * @return
	 */
	List<Seckill> queryAll(@Param("offest")int offest,@Param("limit")int limit);
}
