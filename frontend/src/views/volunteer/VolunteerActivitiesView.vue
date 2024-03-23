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
        <template v-slot:[`item.themes`]="{ item }">
          <v-chip v-for="theme in item.themes" v-bind:key="theme.id">
            {{ theme.completeName }}
          </v-chip>
        </template>
        <template v-slot:[`item.action`]="{ item }">
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
                @click="newEnrollment"
              >fas fa-sign-in-alt</v-icon
              >
            </template>
            <span>Apply for Activity</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>
    <enrollment-dialog
      :dialog="editEnrollmentDialog"
      :activity="currentActivity"
      @close="editEnrollmentDialog = false"
      @apply="applyForActivity"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import { show } from 'cli-cursor';
import EnrollmentDialog from '@/views/member/EnrollmentDialog.vue';

@Component({
  components: {'enrollment-dialog': EnrollmentDialog },
  data() {
    return {
      showEnrollmentDialog: false,
      selectedActivity: null
    };
  },
  methods: { show },
})
export default class VolunteerActivitiesView extends Vue {
  activities: Activity[] = [];
  enrollments: Enrollment[] = [];
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
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newEnrollment() {
    this.currentEnrollment = new Enrollment();
    this.editEnrollmentDialog = true;
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

  async activityEnrollments(activity: Activity) {
    if (activity.id !== null) {
      try {
        const result = await RemoteServices.getActivityEnrollments(activity.id);
        this.activities = this.activities.filter((a) => a.id !== activity.id);
        this.activities.unshift(result);
        return result;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
  shouldShowApplyButton(activity: Activity) {
    // Verifique se o período de candidatura ainda está aberto
    const applicationDeadline = new Date(activity.formattedApplicationDeadline);
    const currentDate = new Date();
    const isApplicationOpen = currentDate <= applicationDeadline; //tinha so < mas deve ser igual tmb - testar como ta agr

    // Check if the current user ID is present
    const userId = this.$store.getters.getUser.id;

    // Check if the current user has already applied to the activity
    const volunteerHasAlreadyEnrolled = this.enrollments.some((enrollment) =>
      enrollment.activityId === activity.id);

    // O botão deve ser mostrado apenas se o período de candidatura estiver aberto
    // e o voluntário ainda não se candidatou
    return isApplicationOpen && !volunteerHasAlreadyEnrolled;
  }

  /*
  async applyForActivity () {
    return;
  }
  */
  async applyForActivity(activity: Activity) {
    try {
      this.currentActivity = activity;
      // Call your RemoteServices method to apply for the activity
      await RemoteServices.createEnrollment(this.currentActivity.id);
      // Optionally, you can refresh the activities or perform any necessary updates
      // Check if the enrollment was successful
      const successfulEnrollment = this.checkSuccessfulEnrollment(this.currentActivity);

      // If the enrollment was successful, close the dialog
      if (successfulEnrollment) {
        this.editEnrollmentDialog = false;
      }
    } catch (error) {
      // Handle errors
    }
  }

  checkSuccessfulEnrollment(activity: Activity) {
    // Check if the current user ID is present
    const userId = this.$store.getters.getUser.id;

    // Check if the current user has already applied to the activity
    const volunteerHasAlreadyEnrolled = this.enrollments.some((enrollment) =>
      enrollment.activityId === activity.id);

    return volunteerHasAlreadyEnrolled;
  }
}
</script>

<style lang="scss" scoped></style>
