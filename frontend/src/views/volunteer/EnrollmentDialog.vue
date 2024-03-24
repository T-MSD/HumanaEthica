<template>
  <v-dialog v-model="dialog" persistent width="500">
    <v-card>
      <v-card-title>
        <span class="headline">Apply for Activity</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-text-field
            label="*Motivation"
            :rules="[(v) => !!v || 'Motivation is required']"
            v-model="newEnrollment.motivation"
            required
          ></v-text-field>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="$emit('close-enrollment-dialog')"
        >
          Cancel
        </v-btn>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="registerEnrollment"
          :disabled="motivationLength < 10"
        >
          Apply
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import Enrollment from '@/models/enrollment/Enrollment';
import RemoteServices from '@/services/RemoteServices';
import Activity from '@/models/activity/Activity';

@Component({})
export default class EnrollmentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Enrollment, required: true }) readonly enrollment!: Enrollment;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;

  newEnrollment: Enrollment = new Enrollment();

  async created() {
    this.newEnrollment = new Enrollment(this.enrollment);
  }

  get motivationLength() {
    return this.newEnrollment.motivation
      ? this.newEnrollment.motivation.length
      : 0;
  }

  get canSave(): boolean {
    return !!this.newEnrollment.motivation;
  }

  async registerEnrollment() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        if (this.activity && this.activity.id !== null) {
          const result = await RemoteServices.createEnrollment(
            this.activity.id,
            this.newEnrollment,
          );
          this.$emit('save-enrollment', result);
        } else {
          throw new Error('Activity ID is null or undefined');
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
