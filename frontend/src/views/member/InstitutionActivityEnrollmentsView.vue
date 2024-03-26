<template>
  <v-card class="table">
    <div class="text-h3">{{ activity.name }}</div>
    <v-data-table
      :headers="headers"
      :items="enrollments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      data-cy="activityEnrollmentsTable"
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
          <v-btn
            color="primary"
            dark
            @click="getActivities"
            data-cy="getActivities"
            >Activities</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:[`item.action`]="{ item }">
        <v-tooltip v-if="!item.participating && !isfull" bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2 action-button"
              @click="createParticipationDialog(item)"
              v-on="on"
              data-cy="createButton"
              >edit</v-icon
            >
          </template>
          <span>Select Participant</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <participation-selection-dialog
      v-if="currentEnrollment && ParticipationDialog"
      v-model="ParticipationDialog"
      v-on:close-participation-dialog="onCloseParticipationDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';
import Enrollment from '@/models/enrollment/Enrollment';
import Participation from '@/models/participation/Participation';
import ParticipationSelectionDialog from '@/views/member/ParticipationSelectionDialog.vue';

@Component({
  components: {
    'participation-selection-dialog': ParticipationSelectionDialog,
  },
})
export default class InstitutionActivityEnrollmentsView extends Vue {
  activity!: Activity;
  enrollments: Enrollment[] = [];
  search: string = '';
  participation: Participation = new Participation();
  currentEnrollment: Enrollment | null = null;
  ParticipationDialog: boolean = false;
  isfull: boolean = true;

  headers: object = [
    {
      text: 'Volunteer Name',
      value: 'volunteerName',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Motivation',
      value: 'motivation',
      align: 'left',
      width: '50%',
    },
    {
      text: 'Participatings',
      value: 'participating',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Application Date',
      value: 'enrollmentDateTime',
      align: 'left',
      width: '5%',
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '5%',
    },
  ];

  async created() {
    this.activity = this.$store.getters.getActivity;
    this.isfull = !(
      this.activity.participantsNumberLimit >
      this.activity.numberOfParticipations
    );
    if (this.activity !== null && this.activity.id !== null) {
      await this.$store.dispatch('loading');
      try {
        this.enrollments = await RemoteServices.getActivityEnrollments(
          this.activity.id,
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async getActivities() {
    await this.$store.dispatch('setActivity', null);
    this.$router.push({ name: 'institution-activities' }).catch(() => {});
  }

  async createParticipation(enrollment: Enrollment) {
    try {
      const result = await RemoteServices.registerParticipation(
        this.$store.getters.getUser.id,
        this.participation,
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  createParticipationDialog(enrollment: Enrollment) {
    this.currentEnrollment = enrollment;
    this.ParticipationDialog = true;
  }

  onCloseParticipationDialog() {
    this.currentEnrollment = null;
    this.ParticipationDialog = false;
  }
}
</script>

<style lang="scss" scoped>
.date-fields-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-fields-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
</style>
