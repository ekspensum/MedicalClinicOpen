import { User } from './user';
import { WorkingWeek } from './working-week';

export class Doctor {

  id: number;
  experience: string;
  pesel: string;
  country: string;
  zipCode: string;
  city: string;
  street: string;
  streetNo: string;
  unitNo: string;
  phone: string;
  registerDateTime: Date;
  editDateTime: Date;

  constructor(public user: User, public workingWeek: WorkingWeek){ }
}
