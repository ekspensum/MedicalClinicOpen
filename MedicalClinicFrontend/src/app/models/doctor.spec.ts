import { Doctor } from './doctor';
import { User } from './user';
import { WorkingWeek } from './working-week';

describe('Doctor', () => {
  it('should create an instance', () => {
    expect(new Doctor(new User(), new WorkingWeek())).toBeTruthy();
  });
});
