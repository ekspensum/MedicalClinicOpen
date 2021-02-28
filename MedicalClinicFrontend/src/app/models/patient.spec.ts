import { Patient } from './patient';
import { User } from './user';

describe('Patient', () => {
  it('should create an instance', () => {
    expect(new Patient(new User())).toBeTruthy();
  });
});
