package com.example.server.controller;

import com.example.server.common.Result;
import com.example.server.dto.AccessAuthorizationDTO;
import com.example.server.entity.AccessAuthorization;
import com.example.server.service.AccessAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门禁授权 Controller
 */
@RestController
@RequestMapping("/api/access")
public class AccessAuthorizationController {

    @Autowired
    private AccessAuthorizationService accessAuthorizationService;

    /**
     * 生成门禁凭证
     */
    @PostMapping("/generate")
    public Result<Long> generateAccessCredential(@RequestBody AccessAuthorizationDTO dto) {
        Long id = accessAuthorizationService.generateAccessCredential(dto);
        return Result.success("门禁凭证生成成功", id);
    }

    /**
     * 查询授权信息
     */
    @GetMapping("/{id}")
    public Result<AccessAuthorization> getAuthorization(@PathVariable Long id) {
        AccessAuthorization auth = accessAuthorizationService.getAuthorizationById(id);
        return Result.success(auth);
    }

    /**
     * 根据二维码查询
     */
    @GetMapping("/qrcode/{qrCode}")
    public Result<AccessAuthorization> getAuthorizationByQRCode(@PathVariable String qrCode) {
        AccessAuthorization auth = accessAuthorizationService.getAuthorizationByQRCode(qrCode);
        return Result.success(auth);
    }

    /**
     * 撤销授权
     */
    @PutMapping("/revoke/{id}")
    public Result<String> revokeAuthorization(@PathVariable Long id) {
        accessAuthorizationService.revokeAuthorization(id);
        return Result.success("授权已撤销");
    }

    /**
     * 查询有效授权
     */
    @GetMapping("/valid/{visitorId}")
    public Result<List<AccessAuthorization>> listValidAuthorizations(@PathVariable Long visitorId) {
        List<AccessAuthorization> auths = accessAuthorizationService.listValidAuthorizations(visitorId);
        return Result.success(auths);
    }

    /**
     * 验证访问权限
     */
    @PostMapping("/validate")
    public Result<Boolean> validateAccess(@RequestParam String qrCode,
                                          @RequestParam String areaCode) {
        boolean valid = accessAuthorizationService.validateAccess(qrCode, areaCode);
        return Result.success(valid ? "权限验证通过" : "权限验证失败", valid);
    }

    /**
     * 查询所有授权列表（分页）
     */
    @GetMapping("/list")
    public Result<List<AccessAuthorization>> listAuthorizations(
            @RequestParam(required = false) String status) {
        List<AccessAuthorization> auths;
        if (status != null) {
            auths = accessAuthorizationService.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AccessAuthorization>()
                    .eq("status", status));
        } else {
            auths = accessAuthorizationService.list();
        }
        return Result.success(auths);
    }
}
