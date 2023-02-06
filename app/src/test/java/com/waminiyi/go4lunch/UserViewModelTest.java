package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.repository.UserRepository;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.Objects;

public class UserViewModelTest {

    FirebaseUser mockedUser = mock(FirebaseUser.class);
    UserRepository userRepository = mock(UserRepository.class);
    UserViewModel userViewModel = new UserViewModel(userRepository);
    UserEntity user = new UserEntity("userId", "userName", "userMail", "userPhone", "urlPicture");

    @Test
    public void getCurrentUserTest() {
        when(userRepository.getCurrentUser()).thenReturn(mockedUser);
        userViewModel.getCurrentUser();
        assertEquals(mockedUser.getUid(), userViewModel.getCurrentUser().getUid());
    }

    @Test
    public void getCurrentUserDataTest() {
        when(userRepository.getCurrentUserData()).thenReturn(new MutableLiveData<>(user));
        assertEquals(user.getuId(), Objects.requireNonNull(userViewModel.getCurrentUserData().getValue()).getuId());
    }

    @Test
    public void logoutTest() {
        doAnswer((Answer<Void>) invocation -> null).when(userRepository).logOut();
        userViewModel.logOut();
        verify(userRepository).logOut();
    }

    @Test
    public void createUserTest() {
        doAnswer((Answer<Void>) invocation -> null).when(userRepository).createNewUserInDatabase(mockedUser);
        userViewModel.createNewUserInDatabase(mockedUser);
        verify(userRepository).createNewUserInDatabase(mockedUser);
    }

    @Test
    public void isUserLoggedTest() {
        when(userRepository.isCurrentUserLogged()).thenReturn(true);
        userViewModel.isCurrentUserLogged();
        verify(userRepository).isCurrentUserLogged();
    }
}
