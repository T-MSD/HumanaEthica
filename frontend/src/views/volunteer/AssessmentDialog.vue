<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editAssessment && editAssessment.id === null
              ? 'New Assessment'
              : 'New Assessment'
          }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12">
              <v-text-field
                label="*Review"
                :rules="[(v) => !!v || 'Description is required']"
                required
                v-model="editAssessment.review"
                data-cy="descriptionInput"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="$emit('close-assessment-dialog')"
        >
          Close
        </v-btn>
        <v-btn
          v-if="checkReviewLength()"
          color="blue-darken-1"
          variant="text"
          @click="writeAssessment"
          data-cy="saveAssessment"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';
import Assessment from '@/models/assessment/Assessment';
import Institution from '@/models/institution/Institution';
import Activity from '@/models/activity/Activity';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
@Component({
  methods: { ISOtoString },
})
export default class AssessmentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Assessment, required: true }) readonly assessment!: Assessment;
  @Prop({ type: Activity, required: true }) activity!: Activity;
  editAssessment: Assessment = new Assessment();
  async created() {
    this.editAssessment = new Assessment(this.assessment);
  }

  checkReviewLength(): boolean {
    return (
      !!this.editAssessment.review && this.editAssessment.review.length >= 10
    );
  }
  async writeAssessment() {
    if (
      (this.$refs.form as Vue & { validate: () => boolean }).validate() &&
      this.editAssessment != null
    ) {
      try {
        const result = await RemoteServices.createAssessment(
          this.activity.institution.id,
          this.editAssessment,
        );
        this.$emit('save-assessment', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped lang="scss"></style>
