import { UserModel } from './user.modal';

describe('User model check', () => {
  it('should conform to UserModel ', () => {
    const fileModel = new UserModel('fre');

    expect(typeof fileModel.preRegId).toEqual('string');
  });
});
