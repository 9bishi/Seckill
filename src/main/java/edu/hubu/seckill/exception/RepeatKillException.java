package edu.hubu.seckill.exception;

/**
 * �ظ���ɱ�쳣(�������쳣)
 * @author sucan
 *
 */

public class RepeatKillException extends SeckillExceprion {
	
	public RepeatKillException(String message){
		super(message);
	}
	
	public RepeatKillException(String message,Throwable cause){
		super(message,cause);
	}
}
