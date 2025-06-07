package Job.controller;


import Job.model.JobSchedule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Job.service.JobService;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController
{
    private final JobService schedulerService;

    public JobController(JobService schedulerService)
    {
        this.schedulerService = schedulerService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<Long> scheduleJob(@RequestBody JobSchedule jobSchedule) {
        Long jobId = schedulerService.scheduleJob(jobSchedule);
        return ResponseEntity.ok(jobId);
    }

    @GetMapping("/list")
    public ResponseEntity<List<JobSchedule>> listJobs() {
        return ResponseEntity.ok(schedulerService.listJobs());
    }

    @DeleteMapping("/cancel/{jobId}")
    public ResponseEntity<String> cancelJob(@PathVariable Long jobId) {
        boolean cancelled = schedulerService.cancelJob(jobId);
        if (cancelled) {
            return ResponseEntity.ok("Job cancelled successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
