package Job.service;


import Job.model.JobSchedule;
import Job.repository.JobRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.time.temporal.TemporalAdjusters;

@Service
public class JobService
{
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private final JobRepository jobRepo;

    public JobService(JobRepository jobRepo) {
        this.jobRepo = jobRepo;
    }

    @PostConstruct
    public void init() {
        // Load jobs from DB and schedule
        jobRepo.findAll().forEach(this::scheduleJobFromDb);
    }

    public Long scheduleJob(JobSchedule jobSchedule) {
        JobSchedule savedJob = jobRepo.save(jobSchedule);
        scheduleJobFromDb(savedJob);
        return savedJob.getId();
    }

    private void scheduleJobFromDb(JobSchedule jobSchedule) {
        Runnable task = () -> System.out.println("Hello World from Job ID " + jobSchedule.getId() + " at " + LocalDateTime.now());

        ScheduledFuture<?> future = null;
        switch (jobSchedule.getScheduleType()) {
            case HOURLY:
                future = scheduleHourly(task, jobSchedule.getMinuteOfHour());
                break;
            case DAILY:
                future = scheduleDaily(task, jobSchedule.getTimeOfDay());
                break;
            case WEEKLY:
                future = scheduleWeekly(task, jobSchedule.getDaysOfWeekSet(), jobSchedule.getTimeOfDayWeekly());
                break;
        }
        scheduledTasks.put(jobSchedule.getId(), future);
    }

    private ScheduledFuture<?> scheduleHourly(Runnable task, Integer minuteOfHour) {
        long delay = computeInitialDelayForHourly(minuteOfHour);
        return scheduler.scheduleAtFixedRate(task, delay, 3600, TimeUnit.SECONDS);
    }

    private long computeInitialDelayForHourly(int minuteOfHour) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withMinute(minuteOfHour).withSecond(0).withNano(0);
        if (!nextRun.isAfter(now)) {
            nextRun = nextRun.plusHours(1);
        }
        return Duration.between(now, nextRun).getSeconds();
    }

    private ScheduledFuture<?> scheduleDaily(Runnable task, LocalTime timeOfDay) {
        long delay = computeInitialDelayForDaily(timeOfDay);
        return scheduler.scheduleAtFixedRate(task, delay, 86400, TimeUnit.SECONDS);
    }

    private long computeInitialDelayForDaily(LocalTime timeOfDay) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(timeOfDay.getHour()).withMinute(timeOfDay.getMinute()).withSecond(0).withNano(0);
        if (!nextRun.isAfter(now)) {
            nextRun = nextRun.plusDays(1);
        }
        return Duration.between(now, nextRun).getSeconds();
    }

    private ScheduledFuture<?> scheduleWeekly(Runnable task, Set<DayOfWeek> daysOfWeek, LocalTime timeOfDay) {
        long initialDelay = computeInitialDelayForWeekly(daysOfWeek, timeOfDay);
        long oneWeekSeconds = 7 * 24 * 60 * 60;

        return scheduler.scheduleAtFixedRate(task, initialDelay, oneWeekSeconds, TimeUnit.SECONDS);
    }

    private long computeInitialDelayForWeekly(Set<DayOfWeek> daysOfWeek, LocalTime timeOfDay) {
        LocalDateTime now = LocalDateTime.now();

        // Find soonest day/time in future from now
        long minDelay = Long.MAX_VALUE;

        for (DayOfWeek day : daysOfWeek) {
            LocalDate nextDate = now.toLocalDate().with(TemporalAdjusters.nextOrSame(day));
            LocalDateTime nextRun = nextDate.atTime(timeOfDay);
            if (!nextRun.isAfter(now)) {
                nextRun = nextRun.plusWeeks(1);
            }
            long delay = Duration.between(now, nextRun).getSeconds();
            if (delay < minDelay) {
                minDelay = delay;
            }
        }

        return minDelay;
    }

    public boolean cancelJob(Long jobId) {
        ScheduledFuture<?> future = scheduledTasks.get(jobId);
        if (future != null) {
            future.cancel(false);
            scheduledTasks.remove(jobId);
            jobRepo.deleteById(jobId);
            return true;
        }
        return false;
    }

    public List<JobSchedule> listJobs() {
        return jobRepo.findAll();
    }
}
