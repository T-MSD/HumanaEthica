<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span class="headline">
          Select Participant
        </span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" lazy-validation>
          <v-row>
            <v-col cols="12">
              <v-text-field
                  label="Rating"
                  :rules="[
                  (v) =>
                    isNumberValid(v) ||
                    'Number of rating must be between 1 and 5',
                ]"
                  data-cy="ratingNumberInput"
                  v-model="createParticipation.rating"
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
            @click="$emit('close-participation-dialog')"
        >
          Close
        </v-btn>
        <v-btn
            color="blue-darken-1"
            variant="text"
            data-cy="makeParticipation"
            @click="makeParticipation"
        >
          Make Participant
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Vue, Component, Prop, Model } from 'vue-property-decorator';
import Participation from '@/models/participation/Participation';
import Enrollment from '@/models/enrollment/Enrollment'
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import { ISOtoString } from '@/services/ConvertDateService';
import Activity from '@/models/activity/Activity';


@Component({
  methods: { ISOtoString },
})
export default class ParticipationSelectionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Participation, required: true }) readonly participation!: Participation;
  @Prop({ type: Activity, required: true }) readonly activity!: Activity;
  @Prop({ type: Enrollment, required: true }) readonly enrollment!: Enrollment;

  createParticipation: Participation = new Participation();


  cypressCondition: boolean = false;

  async created() {
    this.createParticipation = new Participation(this.participation);
  }

  isNumberValid(value: any) {
    if (value == null) return true;
    if (!/^\d+$/.test(value)) return false;
    const parsedValue = parseInt(value);
    return parsedValue >= 1 && parsedValue <= 5;
  }

  async makeParticipation() {
    if ((this.$refs.form as Vue & { validate: () => boolean }).validate()) {
      try {
        if (this.activity && this.activity.id !== null && this.enrollment.volunteerName != null) {
          this.createParticipation.volunteerId = this.enrollment.volunteerID;
          const result = await RemoteServices.registerParticipation(
            this.activity.id,
            this.createParticipation,
          );
          this.$emit('make-participant', result);
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

<style scoped lang="scss"></style>
