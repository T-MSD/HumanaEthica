<template>
  <div>
    <v-card class="table">
      <v-data-table
        :headers="headers"
        :items="activities"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        data-cy="volunteerActivitiesTable"
      >
        <template v-slot:top>
          <v-card-title>
            <v-text-field
              v-model="search"
              append-icon="search"
              label="Search"
              class="mx-2"
            />
            <v-spacer />
          </v-card-title>
        </template>
        <template v-slot:[`item.themes`]="{ item, index }">
          <v-chip v-for="theme in item.themes" v-bind:key="theme.id">
            {{ theme.completeName }} - Index: {{ index }}
          </v-chip>
        </template>
        <template v-slot:[`item.action`]="{ item, index }">
          <v-tooltip v-if="item.state === 'APPROVED'" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                class="mr-2 action-button"
                color="red"
                v-on="on"
                data-cy="reportButton"
                @click="reportActivity(item)"
              >warning</v-icon
              >
            </template>
            <span>Report Activity</span>
          </v-tooltip>
          <v-tooltip v-if="item.state === 'APPROVED' && shouldShowApplyButton(item)" bottom>
            <template v-slot:activator="{ on }">
              <!-- New column for Apply for activity button -->
              <v-icon
                class="mr-2 action-button"
                color="blue"
                v-on="on"
                data-cy="newEnrollment"
                @click="newEnrollment(item)"
              >fas fa-sign-in-alt</v-icon
              >
            </template>
            <span>Apply for Activity</span>
          </v-tooltip>
          <v-tooltip v-if="item.state === 'APPROVED'" bottom>
          <v-tooltip
            v-if="
              activityHasFinished[index] &&
              volunteerHasParticipation[index] &&
              volunteerHasAssessment[index] &&
              item.state === 'APPROVED'
            "
            bottom
          >
            <template v-slot:activator="{ on }">
              <v-icon
                v-if="volunteerHasParticipation[index]"
                class="mr-2 action-button"
                color="green"
                v-on="on"
                data-cy="writeAssessmentButton"
                @click="createAssessment(item, index)"
                >mdi-pencil</v-icon
              >
            </template>
            <span>Write Assessment</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <enrollment-dialog
        v-if="editEnrollmentDialog"
        v-model="editEnrollmentDialog"
        :enrollment="currentEnrollment"
        :activity="currentActivity"
        v-on:save-enrollment="onSaveEnrollment"
        v-on:close-enrollment-dialog="onCloseEnrollmentDialog"
      />
      <assessment-dialog
        v-if="currentActivity && editAssessmentDialog"
        v-model="editAssessmentDialog"
        :assessment="currentAssessment"
        :activity="currentActivity"
        v-on:save-assessment="onSaveAssessment"
        v-on:close-assessment-dialog="onCloseAssessmentDialog"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import EnrollmentDialog from '@/views/volunteer/EnrollmentDialog.vue';

@Component({
  components: {
    'enrollment-dialog': EnrollmentDialog,
  },
import AssessmentDialog from './AssessmentDialog.vue';
import { show } from 'cli-cursor';
import Assessment from '@/models/assessment/Assessment';
import Institution from '@/models/institution/Institution';
import Volunteer from '@/models/volunteer/Volunteer';
import Participation from '@/models/participation/Participation';

@Component({
  methods: { show },
  components: {
    'assessment-dialog': AssessmentDialog,
  },
})
export default class VolunteerActivitiesView extends Vue {
  institution: Institution = new Institution();
  activities: Activity[] = [];
  enrollments: Enrollment[] = [];
  volunteer: Volunteer = new Volunteer();
  participation: Participation[] = [];
  assessmentForVolunteer: Assessment[] = [];
  assessmentForInstitution: Assessment[] = [];
  currentAssessment: Assessment | null = null;
  editAssessmentDialog: boolean = false;
  activityHasFinished: boolean[] = [];
  volunteerHasParticipation: boolean[] = [];
  volunteerHasAssessment: boolean[] = [];
  currentActivity: Activity | null = null;
  currentIndex: number = 0;
  search: string = '';
  currentEnrollment: Enrollment | null = null;
  editEnrollmentDialog: boolean = false;
  currentActivity: Activity | null = null;
  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Region',
      value: 'region',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Participants',
      value: 'participantsNumberLimit',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Themes',
      value: 'themes',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Description',
      value: 'description',
      align: 'left',
      width: '30%',
    },
    {
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Start Date',
      value: 'formattedStartingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'End Date',
      value: 'formattedEndingDate',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Deadline',
      value: 'formattedApplicationDeadline',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '5%',
    },
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.activities = await RemoteServices.getActivities();
      this.enrollments = await  RemoteServices.getVolunteerEnrollments();

      this.assessmentForVolunteer =
        await RemoteServices.getVolunteerAssessments();

      this.participation = await RemoteServices.getVolunteerParticipations();

      this.activityHasFinished = this.activities.map(() => false);
      this.activities.forEach((activity, index) => {
        this.activityFinished(activity, index);
      });

      this.volunteerHasParticipation = this.activities.map(() => false);
      this.activities.forEach(() => {
        this.hasParticipation();
      });

      this.volunteerHasAssessment = this.activities.map(() => true);
      this.activities.forEach((activityItem, index) => {
        this.hasAssessmentForInstitution(
          activityItem,
          index,
          this.assessmentForVolunteer,
        );
      });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newEnrollment(activity : Activity) {
    this.currentEnrollment = new Enrollment();
    this.currentActivity = activity;
    this.editEnrollmentDialog = true;
  }

  async onSaveEnrollment(enrollment: Enrollment) {
    this.enrollments.unshift(enrollment);
    if (this.currentActivity) {
      const successfulEnrollment = this.checkSuccessfulEnrollment(this.currentActivity);
      // If the enrollment was successful, close the dialog
      if (successfulEnrollment) {
        this.editEnrollmentDialog = false;
      }
    }
  }

  onCloseEnrollmentDialog() {
    this.currentEnrollment = null;
    this.editEnrollmentDialog = false;
  }

  async reportActivity(activity: Activity) {
    if (activity.id !== null) {
      try {
        const result = await RemoteServices.reportActivity(
          this.$store.getters.getUser.id,
          activity.id,
        );
        this.activities = this.activities.filter((a) => a.id !== activity.id);
        this.activities.unshift(result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
  shouldShowApplyButton(activity: Activity) {
    // Verifique se o período de candidatura ainda está aberto
    const applicationDeadline = new Date(activity.formattedApplicationDeadline);
    const currentDate = new Date();
    const isApplicationOpen = currentDate <= applicationDeadline;

    // Check if the current user has already applied to the activity
    const volunteerHasAlreadyEnrolled = this.enrollments.some((enrollment) =>
      enrollment.activityId === activity.id);

    // O botão deve ser mostrado apenas se o período de candidatura estiver aberto
    // e o voluntário ainda não se candidatou
    return isApplicationOpen && !volunteerHasAlreadyEnrolled;
  }

  checkSuccessfulEnrollment(activity: Activity) {
    // Check if the current user has already applied to the activity
    const volunteerHasAlreadyEnrolled = this.enrollments.some((enrollment) =>
        enrollment.activityId === activity.id);

    return volunteerHasAlreadyEnrolled;
  }

  async hasAssessmentForInstitution(
    activity: Activity,
    index: number,
    assessments: Assessment[],
  ) {
    try {
      assessments.forEach((assessmentsItem) => {
        if (assessmentsItem.institutionId == activity.institution.id) {
          this.$set(this.volunteerHasAssessment, index, false);
        }
      });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async hasParticipation() {
    try {
      this.activities.forEach((activityItem, activityIndex) => {
        this.participation.forEach((participationItem) => {
          if (participationItem.activityId === activityItem.id) {
            this.$set(this.volunteerHasParticipation, activityIndex, true);
          }
        });
      });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async activityFinished(activity: Activity, index: number) {
    if (activity.id !== null) {
      try {
        const currentDate = new Date();
        const newEndingDate = new Date(activity.endingDate);

        this.$set(this.activityHasFinished, index, newEndingDate < currentDate);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  createAssessment(activity: Activity, index: number) {
    this.currentIndex = index;
    this.currentActivity = activity;
    this.currentAssessment = new Assessment();
    this.editAssessmentDialog = true;
  }
  async onSaveAssessment(assessment: Assessment) {
    this.editAssessmentDialog = false;
    this.currentActivity = null;
    this.volunteerHasParticipation[this.currentIndex] = false;
    this.assessmentForInstitution.unshift(assessment);
    this.assessmentForVolunteer.unshift(assessment);
  }

  onCloseAssessmentDialog() {
    this.currentActivity = null;
    this.editAssessmentDialog = false;
  }
}
</script>

<style lang="scss" scoped></style>
