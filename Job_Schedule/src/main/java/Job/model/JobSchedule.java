package Job.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "job_schedule")
public class JobSchedule {

    public enum ScheduleType {
        HOURLY, DAILY, WEEKLY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    private Integer minuteOfHour;

    private LocalTime timeOfDay;

    private String daysOfWeek;

    private LocalTime timeOfDayWeekly;

    // 🔄 Required Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Integer getMinuteOfHour() {
        return minuteOfHour;
    }

    public void setMinuteOfHour(Integer minuteOfHour) {
        this.minuteOfHour = minuteOfHour;
    }

    public LocalTime getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(LocalTime timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public LocalTime getTimeOfDayWeekly() {
        return timeOfDayWeekly;
    }

    public void setTimeOfDayWeekly(LocalTime timeOfDayWeekly) {
        this.timeOfDayWeekly = timeOfDayWeekly;
    }

    // Convert comma-separated string to Set<DayOfWeek>
    public Set<DayOfWeek> getDaysOfWeekSet() {
        if (daysOfWeek == null || daysOfWeek.isEmpty()) return Set.of();
        return Arrays.stream(daysOfWeek.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }

    // 🔄 Required default constructor for JPA
    public JobSchedule() {}
}
