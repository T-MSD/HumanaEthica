package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public List<EnrollmentDto> getActivityEnrollments(@PathVariable int activityId) {
        return enrollmentService.getEnrollmentsByActivity(activityId);
    }

    @PostMapping()
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public EnrollmentDto registerEnrollment(@PathVariable int volunteerId, 
    @PathVariable int activityId , @Valid @RequestBody EnrollmentDto enrollmentDto){

        return enrollmentService.registerEnrollment(volunteerId, activityId, enrollmentDto);
    }
   



}