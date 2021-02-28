import { Referral } from "./referral";

export class Diagnosis {

  id: number;
  ailments: string;
  orders: string;
  medicines: string;
  registerDateTime: Date;
  updateDateTime: Date;
  referralList: Array<Referral>;

  constructor() { }
}
