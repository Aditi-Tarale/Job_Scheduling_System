package Job.repository;

import Job.model.JobSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<JobSchedule, Long>
{
}
