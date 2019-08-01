package com.hiveel.autossav.service;

import com.hiveel.autossav.model.ProjectRestCode;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.SecurityCode;
import com.hiveel.autossav.model.entity.SecurityCodeType;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.exception.ServiceException;

import java.util.List;

public interface SecurityCodeService {
    int save(SecurityCode e);

    SecurityCode saveCodeByPerson(Person person, SecurityCodeType type);

    String getName(Person person);

    boolean delete(SecurityCode e);

    boolean update(SecurityCode e);

    SecurityCode findById(SecurityCode e);

    int count(SearchCondition searchCondition);

    List<SecurityCode> find(SearchCondition searchCondition);

    /**
     * 发送安全码到某邮箱或手机短信
     * @param e
     * @throws FailException
     */
    void sendCode(SecurityCode e) throws FailException;

    /**
     * 检查一个更新用户操作的安全码
     * 如果验证通过，更新这个安全码状态为已经使用
     * 该方法嵌入在其他用户相关的更新事务中
     * @param code
     * @param name
     * @param type
     * @return 验证码验证结果
     */
    void verifyCodeAndUsed(String code, String name, SecurityCodeType type) throws FailException, ServiceException;
}
