package coursework_manager.rmi_interfaces;

import coursework_manager.models.Mark;
import coursework_manager.models.users.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Optional;

public interface ILoginRepo extends Remote {
    User login(User user) throws RemoteException;
}
