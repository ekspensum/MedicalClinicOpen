import { Company } from './company';
import { Owner } from './owner';
import { User } from './user';

describe('Owner', () => {
  it('should create an instance', () => {
    expect(new Owner(new User(), new Company())).toBeTruthy();
  });
});
