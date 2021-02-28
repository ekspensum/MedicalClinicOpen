import { Employee } from './employee';
import { User } from './user';

describe('Employee', () => {
  it('should create an instance', () => {
    expect(new Employee(new User())).toBeTruthy();
  });
});
