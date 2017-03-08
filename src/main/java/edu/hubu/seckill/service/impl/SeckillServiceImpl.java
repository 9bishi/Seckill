package edu.hubu.seckill.service.impl;

import java.util.Date;
import java.util.List;

import javax.xml.ws.soap.Addressing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import edu.hubu.seckill.dao.SeckillDao;
import edu.hubu.seckill.dao.SeckillSuccessDao;
import edu.hubu.seckill.dao.cache.RedisDao;
import edu.hubu.seckill.dto.Exposer;
import edu.hubu.seckill.dto.SeckillExecution;
import edu.hubu.seckill.entity.Seckill;
import edu.hubu.seckill.entity.SeckillSuccess;
import edu.hubu.seckill.enums.SeckillStatEnum;
import edu.hubu.seckill.exception.RepeatKillException;
import edu.hubu.seckill.exception.SeckillCloseException;
import edu.hubu.seckill.exception.SeckillExceprion;
import edu.hubu.seckill.service.SeckillService;

@Service
public class SeckillServiceImpl implements SeckillService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	//ע��service����
	@Autowired
	private SeckillDao seckillDao;
	
	@Autowired
	private SeckillSuccessDao seckillSuccessDao;
	
	@Autowired
	private RedisDao redisDao;
	
	//MD5��ֵ�ַ��������ڻ���MD5
	private final String slat = "sdasdsddsDERTTRTR#$%%DR";

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
		 
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
		
	}

	public Exposer exportSeckillUrl(long seckillId) {
		//�Ż���:�����Ż�:��ʱ�Ļ�����ά��һ����
		/**
		 * get from cache
		 * if null
		 * 	get db
		 * 	else
		 * 		put cache
		 * logic
		 */
		//1. ����dedis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){
			//2.�������ݿ�
			seckill = seckillDao.queryById(seckillId);
			if(seckill != null){
				//3.����redis
				redisDao.putSeckill(seckill);
			}else{
				//�����ɱ������
				return new Exposer(false, seckillId);
			}
		}
		
		Date start =seckill.getStartTime();
		Date end = seckill.getEndTime();
		Date now = new Date();
		//��ɱ���������Ѿ�����
		if(start.getTime() > now.getTime() || end.getTime() < now.getTime()){
			return new Exposer(false, seckillId, now.getTime(),start.getTime(),end.getTime());
		}
		//ת���ض��ַ����Ĺ��̣�������
		String md5 = getMD5(seckillId); //TODO
		return new Exposer(true, md5, seckillId);
	}
	
	//����md5
	private String getMD5(long seckillId){
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
	@Transactional
	/**
	 * ʹ��ע��������񷽷����ŵ�:
	 * 1.�����ŶӴ��һ��Լ��,��ȷ��ע���񷽷��ı�̷��
	 * 2.��֤���񷽷���ִ��ʱ�価���ܶ�,��Ҫ���������������RPC/HTTP������߰��뵽���񷽷��ⲿ
	 * 3.�������еķ�������Ҫ������ֻ��һ���޸Ĳ�����ֻ����������Ҫ������ơ�
	 */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillExceprion, SeckillCloseException, RepeatKillException {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillExceprion("seckill data rewrite");
		}
		//ִ����ɱ�߼�:����棬�Ӽ�¼��ɱ��Ϊ
		Date now = new Date();
		//�����
		try {
			int updateCount = seckillDao.reduceNumber(seckillId,now);
			if(updateCount <= 0){
				//û�и��¼�¼����ɱ����
				throw new SeckillCloseException("seckill is closed");	
			}else{
				//��¼������Ϊ
				int insertCount = seckillSuccessDao.insertSuccessKill(seckillId, userPhone);
				if(insertCount <= 0){
					throw new RepeatKillException("seckill repeated");
				}else{
					//��ɱ�ɹ�
					SeckillSuccess seckillSuccess = seckillSuccessDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, seckillSuccess);
				}
			}
		} catch (SeckillCloseException e1) {
			throw e1;
		}catch (RepeatKillException e2) {
			throw e2;
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			//���б������쳣ת�����������쳣
			throw new SeckillExceprion("seckill inner error:"+e.getMessage());
		}
	}

}
