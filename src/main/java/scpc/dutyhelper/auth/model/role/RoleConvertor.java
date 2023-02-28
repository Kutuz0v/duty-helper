package scpc.dutyhelper.auth.model.role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


@Converter(autoApply = true)
public class RoleConvertor implements AttributeConverter<ERole, String> {

    @Override
    public String convertToDatabaseColumn(ERole role) {
        return role.getValue();
    }

    @Override
    public ERole convertToEntityAttribute(String dbData) {
        return ERole.fromValue(dbData);
    }

}