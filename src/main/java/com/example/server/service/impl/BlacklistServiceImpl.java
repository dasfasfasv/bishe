package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.dto.BlacklistDTO;
import com.example.server.entity.Blacklist;
import com.example.server.mapper.BlacklistMapper;
import com.example.server.service.BlacklistService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 黑名单Service实现类
 */
@Service
public class BlacklistServiceImpl extends ServiceImpl<BlacklistMapper, Blacklist> implements BlacklistService {

    @Override
    public Long addBlacklist(BlacklistDTO dto) {
        Blacklist blacklist = new Blacklist();
        BeanUtils.copyProperties(dto, blacklist);
        blacklist.setStatus("1");
        blacklist.setCreateTime(LocalDateTime.now());
        blacklist.setUpdateTime(LocalDateTime.now());

        save(blacklist);
        return blacklist.getId();
    }

    @Override
    public void removeBlacklist(Long id) {
        Blacklist blacklist = getById(id);
        if (blacklist != null) {
            blacklist.setStatus("0");
            blacklist.setUpdateTime(LocalDateTime.now());
            updateById(blacklist);
        }
    }

    @Override
    public boolean isInBlacklist(Long visitorId) {
        QueryWrapper<Blacklist> wrapper = new QueryWrapper<>();
        wrapper.eq("visitor_id", visitorId)
                .eq("status", "1");

        Blacklist blacklist = getOne(wrapper);
        if (blacklist == null) {
            return false;
        }

        // 检查临时黑名单是否过期
        if ("2".equals(blacklist.getType()) && blacklist.getEndTime() != null) {
            if (LocalDateTime.now().isAfter(blacklist.getEndTime())) {
                removeBlacklist(blacklist.getId());
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isInBlacklistByIdCard(String idCard) {
        QueryWrapper<Blacklist> wrapper = new QueryWrapper<>();
        wrapper.eq("id_card", idCard)
                .eq("status", "1");

        Blacklist blacklist = getOne(wrapper);
        if (blacklist == null) {
            return false;
        }

        // 检查临时黑名单是否过期
        if ("2".equals(blacklist.getType()) && blacklist.getEndTime() != null) {
            if (LocalDateTime.now().isAfter(blacklist.getEndTime())) {
                removeBlacklist(blacklist.getId());
                return false;
            }
        }

        return true;
    }

    @Override
    public List<Blacklist> listBlacklist(String type) {
        QueryWrapper<Blacklist> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "1");
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        wrapper.orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public void updateBlacklist(BlacklistDTO dto) {
        Blacklist blacklist = new Blacklist();
        BeanUtils.copyProperties(dto, blacklist);
        blacklist.setUpdateTime(LocalDateTime.now());
        updateById(blacklist);
    }
}
