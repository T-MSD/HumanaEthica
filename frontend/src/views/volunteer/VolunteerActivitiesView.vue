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

    this.activities.forEach((activity, index) => {
      this.activityFinished(activity, index);
    });

    this.activities.forEach(() => {
      this.hasParticipation();
    });

    this.activities.forEach((activityItem, index) => {
      this.hasAssessmentForInstitution(
        activityItem,
        index,
        this.assessmentForVolunteer,
      );
    });
  }

  onCloseAssessmentDialog() {
    this.currentActivity = null;
    this.editAssessmentDialog = false;
  }
}
</script>

<style lang="scss" scoped></style>
