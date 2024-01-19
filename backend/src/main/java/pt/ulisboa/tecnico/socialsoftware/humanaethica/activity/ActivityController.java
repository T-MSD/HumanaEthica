package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;

import java.security.Principal;
import java.util.List;

@RestController
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @GetMapping("/activities")
    public List<ActivityDto> getActivities() {
        return activityService.getActivities();
    }

    @PostMapping("/activity/register")
    @PreAuthorize("(hasRole('ROLE_MEMBER'))")
    public ActivityDto registerActivity(Principal principal, @Valid @RequestBody ActivityDto activityDto){
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return activityService.registerActivity(userId, activityDto);
    }

    @PutMapping("/activity/{activityId}/update")
    @PreAuthorize("hasRole('ROLE_MEMBER') and hasPermission(#activityId, 'ACTIVITY.MEMBER')")
    public ActivityDto updateActivity(@PathVariable int activityId, @Valid @RequestBody ActivityDto activityDto){
        return activityService.updateActivity(activityId, activityDto);
    }

    @PutMapping("/activity/{activityId}/report")
    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    public ActivityDto reportActivity(@PathVariable int activityId) {
        return activityService.reportActivity(activityId);
    }

    @PutMapping("/activity/{activityId}/suspend")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ActivityDto suspendActivity(@PathVariable Integer activityId) {
        return activityService.suspendActivity(activityId);
    }

    @PutMapping("/activity/{activityId}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ActivityDto validateActivity(@PathVariable int activityId) {
        return activityService.validateActivity(activityId);
    }
}
