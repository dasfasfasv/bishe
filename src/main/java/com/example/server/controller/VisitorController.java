package com.example.server.controller;

import com.example.server.common.Result;
import com.example.server.dto.VisitorDTO;
import com.example.server.entity.Visitor;
import com.example.server.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 访客Controller
 */
@RestController
@RequestMapping("/api/visitor")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    /**
     * 新增访客
     */
    @PostMapping("/add")
    public Result<Long> addVisitor(@RequestBody VisitorDTO dto) {
        Long id = visitorService.saveVisitor(dto);
        return Result.success("访客添加成功", id);
    }

    /**
     * 查询访客详情
     */
    @GetMapping("/{id}")
    public Result<Visitor> getVisitor(@PathVariable Long id) {
        Visitor visitor = visitorService.getVisitorById(id);
        return Result.success(visitor);
    }

    /**
     * 根据身份证查询访客
     */
    @GetMapping("/idcard/{idCard}")
    public Result<Visitor> getVisitorByIdCard(@PathVariable String idCard) {
        Visitor visitor = visitorService.getVisitorByIdCard(idCard);
        return Result.success(visitor);
    }

    /**
     * 更新访客信息
     */
    @PutMapping("/update")
    public Result<String> updateVisitor(@RequestBody VisitorDTO dto) {
        visitorService.updateVisitor(dto);
        return Result.success("访客信息更新成功");
    }

    /**
     * 删除访客
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteVisitor(@PathVariable Long id) {
        visitorService.deleteVisitor(id);
        return Result.success("访客删除成功");
    }

    /**
     * 查询访客列表
     */
    @GetMapping("/list")
    public Result<List<Visitor>> listVisitors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        List<Visitor> visitors = visitorService.listVisitors(name, category);
        return Result.success(visitors);
    }
}
