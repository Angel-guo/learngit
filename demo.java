package com.nyh.service.user.utils;

import com.nyh.common.utils.RandomStringUtils;
import com.nyh.service.user.common.UserCodeAndMessageEnum;
import com.nyh.service.user.feignapi.SerialNumberFeignApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nyh.common.excetions.base.BusinessException;
import com.nyh.service.base.apis.request.SerialNumberReq;
import com.nyh.service.base.apis.response.SerialNumberRes;

import io.seata.common.util.StringUtils;

import java.util.Random;

/**
 * @Author: 刘阳
 * @CreateTime: 2020-08-28 10:08
 */
@Component
public class GenerateNumber {

    @Autowired
    private SerialNumberFeignApi serialNumberFeignApi;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String generateNumber(String key) {
        if (StringUtils.isBlank(key)) {
            logger.error("生成编号key值错误：key = {}", key);
            throw BusinessException.fail(UserCodeAndMessageEnum.GENERATE_NUMBER_ERROR);
        }
        Random random = new Random();
        SerialNumberReq serialNumberReq = new SerialNumberReq(key,
                Integer.parseInt(RandomStringUtils.getRandomNumberString(3)), getRandomNumber(1, 5));
        SerialNumberRes serialNumberRes = serialNumberFeignApi.generateSerialNumber(serialNumberReq);
        if (serialNumberRes == null || serialNumberRes.getSerialNo() == null) {
            logger.error("生成编号失败：key = {}", key);
            throw BusinessException.fail(UserCodeAndMessageEnum.GENERATE_NUMBER_ERROR);
        }
        String number = String.format("%s%04d", key, serialNumberRes.getSerialNo());
        logger.info("生成编号：{}", number);
        return number;
    }

    private int getRandomNumber(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

}

