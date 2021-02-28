import { Role } from './role';

export class User {

  id: number;
  username: string;
  passwordField: string;
  enabled: boolean;
  firstName: string;
  lastName: string;
  email: string;
  language: string;
  photo: string;
  photoString: any;

  constructor(
    public roles?: Array<Role>
  ) { }

}
