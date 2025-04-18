package coursework_manager.repos;


import coursework_manager.rmi_interfaces.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ReposManager {
    private static IGroupRepo groupRepo;
    private static IMarkRepo markRepo;
    private static IRecordRepo recordRepo;
    private static ITeacherRepo teacherRepo;
    private static ILoginRepo loginRepo;

    public static void initializeRegistry() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            groupRepo = (IGroupRepo) registry.lookup("GroupRepo");
            markRepo = (IMarkRepo) registry.lookup("MarkRepo");
            recordRepo = (IRecordRepo) registry.lookup("RecordRepo");
            teacherRepo = (ITeacherRepo) registry.lookup("TeacherRepo");
            loginRepo = (ILoginRepo) registry.lookup("LoginRepo");
        } catch (RemoteException e) {
            throw new RuntimeException("Ошибка подключения к RMI-регистру", e);
        } catch (NotBoundException e) {
            throw new RuntimeException("Не удалось найти один из репозиториев в регистре", e);
        }
    }

    public static IGroupRepo getGroupRepo() {
        if (groupRepo == null) {
            initializeRegistry();
        }
        return groupRepo;
    }

    public static IMarkRepo getMarkRepo() {
        if (markRepo == null) {
            initializeRegistry();
        }
        return markRepo;
    }

    public static IRecordRepo getRecordRepo() {
        if (recordRepo == null) {
            initializeRegistry();
        }
        return recordRepo;
    }

    public static ITeacherRepo getTeacherRepo() {
        if (teacherRepo == null) {
            initializeRegistry();
        }
        return teacherRepo;
    }

    public static ILoginRepo getLoginRepo() {
        if (loginRepo == null) {
            initializeRegistry();
        }
        return loginRepo;
    }

}