import { User } from './user';

export class Employee {

  id: number;
  phone: string;
  pesel: string;
  registerDateTime: Date;
  editDateTime: Date;

  constructor(public user: User) { }

}
