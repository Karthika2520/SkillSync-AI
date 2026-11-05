package com.skillsync.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillsync.backend.dto.RoadmapTaskResponse;
import com.skillsync.backend.dto.UpdateTaskStatusRequest;
import com.skillsync.backend.entity.RoadmapTask;
import com.skillsync.backend.service.RoadmapService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/analysis")
public class RoadmapController 
{
    private final RoadmapService ser;
    public RoadmapController(RoadmapService ser)
    {
        this.ser=ser;
    }

    @PostMapping("/{id}/roadmap")
    public ResponseEntity<List<RoadmapTaskResponse>> createRoadmap(@PathVariable Long id,@RequestParam(defaultValue = "false") boolean replace)
    {
        List<RoadmapTaskResponse> res=ser.generateRoadmap(id,replace);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{id}/roadmap")
    public ResponseEntity<List<RoadmapTaskResponse>> getallroadmaps(@PathVariable Long id)
    {
        List<RoadmapTaskResponse> res=ser.getRoadmap(id);
        return ResponseEntity.ok(res);
    }

     // Update roadmap task status
    @PatchMapping("/roadmap-tasks/{taskId}/status")
    public ResponseEntity<RoadmapTaskResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {

        RoadmapTaskResponse updatedTask = ser.updateTaskStatus(
                taskId, request.getStatus());

        return ResponseEntity.ok(updatedTask);
    }


}
