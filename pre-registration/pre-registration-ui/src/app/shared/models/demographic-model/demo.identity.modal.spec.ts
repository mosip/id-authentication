import { DemoIdentityModel } from './demo.identity.modal';
import { IdentityModel } from './identity.modal';
import { AttributeModel } from './attribute.modal';

describe('Applicant interface check', () => {
  it('should conform to demo interface', () => {
    const attributeModel = new AttributeModel('fre', 'franch');
    const identity = new IdentityModel(
      1,
      [attributeModel],
      'string',
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      'string',
      'string',
      'string',
      'string'
    );
    const applicant = new DemoIdentityModel(identity);

    console.log('typeof applicant.identity', typeof applicant.identity);

    expect(typeof applicant.identity).toEqual('object');
  });
});
