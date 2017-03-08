package edu.hubu.seckill.dao;

import org.apache.ibatis.annotations.Param;

import edu.hubu.seckill.entity.SeckillSuccess;

public interface SeckillSuccessDao {
	
	
	/**
	 * ��ɱ�ɹ�����ɱ��ϸ���в���һ����¼���ɹ����ظ�
	 * @param seckillId
	 * @param userPhone
	 * @return  ���������
	 */
	int insertSuccessKill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
	
	/**
	 * 
	 * @param seckillId
	 * @return ����id��ѯSuccessKilled��Я����ɱ��Ʒʵ�����
	 */
	SeckillSuccess queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
}
