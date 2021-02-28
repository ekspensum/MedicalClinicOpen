import { User } from './user';
import { Company } from './company';

export class Owner {

  id: number;
  phone: string;

  constructor(public user: User, public company: Company) { }

}
