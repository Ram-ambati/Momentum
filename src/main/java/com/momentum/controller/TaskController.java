package com.momentum.controller;

import com.momentum.entity.*;
import com.momentum.service.TaskService;
import com.momentum.service.UserContextService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserContextService userContextService;

    public TaskController(TaskService taskService, UserContextService userContextService) {
        this.taskService = taskService;
        this.userContextService = userContextService;
    }

    @PostMapping
    public TemplateResponse create(@RequestBody @Valid CreateTaskBody body) {
        TaskTemplate template = taskService.createTemplate(userContextService.requireUser(), body.toRequest());
        return TemplateResponse.from(template);
    }

    @GetMapping("/today")
    public List<TaskInstanceResponse> today() {
        return taskService.todayTasks(userContextService.requireUser()).stream().map(TaskInstanceResponse::from).toList();
    }

    @GetMapping("/templates")
    public List<TemplateResponse> templates() {
        return taskService.templates(userContextService.requireUser()).stream().map(TemplateResponse::from).toList();
    }

    @GetMapping("/history/{date}")
    public List<TaskInstanceResponse> byDate(@PathVariable LocalDate date) {
        return taskService.historyForDate(userContextService.requireUser(), date).stream().map(TaskInstanceResponse::from).toList();
    }

    @PostMapping("/{id}/complete")
    public TaskInstanceResponse complete(@PathVariable Long id) {
        return TaskInstanceResponse.from(taskService.completeTask(userContextService.requireUser(), id));
    }

    @PatchMapping("/{id}/status")
    public TaskInstanceResponse updateStatus(@PathVariable Long id, @RequestBody @Valid UpdateStatusBody body) {
        return TaskInstanceResponse.from(taskService.updateStatus(userContextService.requireUser(), id, body.status()));
    }

    @PatchMapping("/templates/{id}")
    public TemplateResponse updateTemplate(@PathVariable Long id, @RequestBody @Valid UpdateTemplateBody body) {
        return TemplateResponse.from(taskService.updateTemplate(userContextService.requireUser(), id, body.toRequest()));
    }

    @DeleteMapping("/templates/{id}")
    public TemplateResponse deactivateTemplate(@PathVariable Long id) {
        return TemplateResponse.from(taskService.deactivateTemplate(userContextService.requireUser(), id));
    }

    public record CreateTaskBody(
        @NotBlank String title,
        String description,
        String category,
        Priority priority,
        @NotNull TaskType taskType,
        RecurrenceRule recurrenceRule,
        LocalDate scheduledDate,
        @Min(0) int xpReward,
        @Min(0) int coinReward
    ) {
        TaskService.CreateTaskRequest toRequest() {
            return new TaskService.CreateTaskRequest(title, description, category, priority, taskType, recurrenceRule, scheduledDate, xpReward, coinReward);
        }
    }

    public record UpdateStatusBody(@NotNull TaskStatus status) {}

    public record UpdateTemplateBody(
        String title,
        String description,
        String category,
        Priority priority,
        TaskType taskType,
        RecurrenceRule recurrenceRule,
        LocalDate scheduledDate,
        @Min(0) Integer xpReward,
        @Min(0) Integer coinReward,
        Boolean active
    ) {
        TaskService.UpdateTaskTemplateRequest toRequest() {
            return new TaskService.UpdateTaskTemplateRequest(
                title, description, category, priority, taskType, recurrenceRule, scheduledDate, xpReward, coinReward, active
            );
        }
    }

    public record TemplateResponse(Long id, String title, String description, String category, Priority priority, TaskType taskType, RecurrenceRule recurrenceRule, LocalDate scheduledDate, int xpReward, int coinReward, boolean active) {
        static TemplateResponse from(TaskTemplate t) {
            return new TemplateResponse(t.getId(), t.getTitle(), t.getDescription(), t.getCategory(), t.getPriority(), t.getTaskType(), t.getRecurrenceRule(), t.getScheduledDate(), t.getXpReward(), t.getCoinReward(), t.isActive());
        }
    }

    public record TaskInstanceResponse(Long id, Long templateId, String title, String category, Priority priority, LocalDate taskDate, TaskStatus status, int xpReward, int coinReward) {
        static TaskInstanceResponse from(TaskInstance i) {
            String title = i.getTitleSnapshot() != null ? i.getTitleSnapshot() : i.getTemplate().getTitle();
            String category = i.getCategorySnapshot() != null ? i.getCategorySnapshot() : i.getTemplate().getCategory();
            Priority priority = i.getPrioritySnapshot() != null ? i.getPrioritySnapshot() : i.getTemplate().getPriority();
            return new TaskInstanceResponse(i.getId(), i.getTemplate().getId(), title, category, priority, i.getTaskDate(), i.getStatus(), i.getXpReward(), i.getCoinReward());
        }
    }
}
