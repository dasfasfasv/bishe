package com.example.server.controller;

import com.example.server.common.Result;
import com.example.server.dto.BlacklistDTO;
import com.example.server.entity.Blacklist;
import com.example.server.service.BlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 黑名单Controller
 */
@RestController
@RequestMapping("/api/blacklist")
public class BlacklistController {

    @Autowired
    private BlacklistService blacklistService;

    /**
     * 添加黑名单
     */
    @PostMapping("/add")
    public Result<Long> addBlacklist(@RequestBody BlacklistDTO dto) {
        Long id = blacklistService.addBlacklist(dto);
        return Result.success("黑名单添加成功", id);
    }

    /**
     * 移除黑名单
     */
    @DeleteMapping("/{id}")
    public Result<String> removeBlacklist(@PathVariable Long id) {
        blacklistService.removeBlacklist(id);
        return Result.success("黑名单已移除");
    }

    /**
     * 查询黑名单列表
     */
    @GetMapping("/list")
    public Result<List<Blacklist>> listBlacklist(
            @RequestParam(required = false) String type) {
        List<Blacklist> blacklists = blacklistService.listBlacklist(type);
        return Result.success(blacklists);
    }

    /**
     * 检查访客是否在黑名单中
     */
    @GetMapping("/check/{visitorId}")
    public Result<Boolean> checkBlacklist(@PathVariable Long visitorId) {
        boolean inBlacklist = blacklistService.isInBlacklist(visitorId);
        return Result.success(inBlacklist ? "访客在黑名单中" : "访客不在黑名单中", inBlacklist);
    }

    /**
     * 根据身份证检查黑名单
     */
    @GetMapping("/check-idcard/{idCard}")
    public Result<Boolean> checkBlacklistByIdCard(@PathVariable String idCard) {
        boolean inBlacklist = blacklistService.isInBlacklistByIdCard(idCard);
        return Result.success(inBlacklist ? "访客在黑名单中" : "访客不在黑名单中", inBlacklist);
    }

    /**
     * 更新黑名单
     */
    @PutMapping("/update")
    public Result<String> updateBlacklist(@RequestBody BlacklistDTO dto) {
        blacklistService.updateBlacklist(dto);
        return Result.success("黑名单更新成功");
    }
}
