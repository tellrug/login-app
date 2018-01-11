package at.vulperium.login.transformer;

import at.vulperium.login.dto.UserDatenDTO;
import at.vulperium.login.entities.User;
import at.vulperium.login.enums.UserStatus;
import at.vulperium.login.utils.TransformerBothDirections;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by 02ub0400 on 10.07.2017.
 */
@ApplicationScoped
public class UserDTOTransformer implements TransformerBothDirections<User, UserDatenDTO> {

    @Override
    public UserDatenDTO transform(User source) {
        return transform(source, new UserDatenDTO());
    }

    @Override
    public UserDatenDTO transform(User source, UserDatenDTO target) {
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setUserStatus(source.getAktiv() == null || !source.getAktiv() ? UserStatus.INAKTIV : UserStatus.AKTIV);
        return target;
    }

    @Override
    public User transformInverse(UserDatenDTO source) {
        return transformInverse(source, new User());
    }

    @Override
    public User transformInverse(UserDatenDTO source, User target) {
        target.setUsername(source.getUsername());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setAktiv(source.getUserStatus() != null && UserStatus.AKTIV == source.getUserStatus());

        return target;
    }
}
