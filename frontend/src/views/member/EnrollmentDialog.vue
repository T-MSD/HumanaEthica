<template>
  <v-dialog v-model="dialog" persistent width="500">
    <v-card>
      <v-card-title>
        <span class="headline">Apply for Activity</span>
      </v-card-title>
      <v-card-text>
        <v-text-field
          label="Motivation"
          v-model="motivation"
          :rules="[(v) => !!v || 'Motivation is required']"
          required
        ></v-text-field>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="closeDialog"
        >
          Cancel
        </v-btn>
        <v-btn
          color="blue-darken-1"
          variant="text"
          @click="createEnrollment"
          :disabled="motivation.length < 10 || !motivation.trim()"
        >
          Apply
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  props: {
    dialog: Boolean,
    activity: Object
  },
  data() {
    return {
      motivation: ''
    };
  },
  computed: {
    isValid() {
      return !!this.motivation;
    }
  },
  methods: {
    closeDialog() {
      this.$emit('close');
    },
    createEnrollment() {
      // Call createEnrollment method and pass the necessary data
      this.$emit('apply', { activityId: this.activity.id, motivation: this.motivation });
      // Close the dialog
      this.closeDialog();
    },
    apply() {
      if (this.isValid) {
        this.$emit('apply', { activityId: this.activity.id, motivation: this.motivation });
        this.closeDialog();
      }
    }
  }
};
</script>
