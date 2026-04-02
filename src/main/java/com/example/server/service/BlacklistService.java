package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.dto.BlacklistDTO;
import com.example.server.entity.Blacklist;

import java.util.List;

/**
 * 黑名单Service接口
 */
public interface BlacklistService extends IService<Blacklist> {
    /**
     * 添加黑名单
     */
    Long addBlacklist(BlacklistDTO dto);

    /**
     * 移除黑名单
     */
    void removeBlacklist(Long id);

    /**
     * 检查是否在黑名单中
     */
    boolean isInBlacklist(Long visitorId);

    /**
     * 根据身份证检查黑名单
     */
    boolean isInBlacklistByIdCard(String idCard);

    /**
     * 查询黑名单列表
     */
    List<Blacklist> listBlacklist(String type);

    /**
     * 更新黑名单
     */
    void updateBlacklist(BlacklistDTO dto);
}
