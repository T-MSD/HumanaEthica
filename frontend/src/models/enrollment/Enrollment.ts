import { ISOtoString } from '@/services/ConvertDateService';

export default class Enrollment {
  id: number | null = null;
  motivation!: string;
  enrollmentDateTime!: string;
  volunteerName!: string;
  participating!: boolean;
  volunteerID!: number;

  constructor(jsonObj?: Enrollment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.motivation = jsonObj.motivation;
      this.enrollmentDateTime = ISOtoString(jsonObj.enrollmentDateTime);
      this.participating = jsonObj.participating;
      this.volunteerName = jsonObj.volunteerName;
      this.volunteerID = jsonObj.volunteerID;
    }
  }
}
