import { MedicalDocument } from './medical-document';
import { User } from './user';

export class Patient {

  id: number;
  socialUserId: string;
  gender: string;
  country: string;
  zipCode: string;
  city: string;
  street: string;
  streetNo: string;
  unitNo: string;
  phone: string;
  pesel: string;
  registerDateTime: Date;
  editDateTime: Date;
  medicalDocumentList: Array<MedicalDocument>;

  constructor(public user: User){ }

}
