package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    UserRepository mockedUserRepository = mock (UserRepository.class);
    UserViewModel mockedUserViewModel = mock(UserViewModel.class);
    UserEntity user = new UserEntity("userId", "userName", "userMail", "userPhone", "urlPicture");
    @Test
    public void getCurrentUserTest() {

        when(mockedUserViewModel.getCurrentUser()).thenReturn(mockedUser);
        mockedUserViewModel.getCurrentUser();
        verify(mockedUserRepository).getCurrentUser();
    }

    @Test
    public void getCurrentUserDataTest() {
        when(mockedUserViewModel.getCurrentUserData()).thenReturn(new MutableLiveData<>(user));

        assertEquals("userId", Objects.requireNonNull(mockedUserViewModel.getCurrentUserData().getValue()).getuId());
    }

    @Test
    public void logoutTest() {

        final boolean[] isUserLogged = {true};

        doAnswer((Answer<Void>) invocation -> {
            isUserLogged[0] = false;
            return null;
        }).when(mockedUserViewModel).logOut();

        mockedUserViewModel.logOut();
        assertFalse(isUserLogged[0]);
    }



    @Test
    public void createUserTest() {
        final boolean[] isUserCreated = {false};

        doAnswer((Answer<Void>) invocation -> {
            isUserCreated[0] = true;
            return null;
        }).when(mockedUserViewModel).createNewUser(mockedUser);

        mockedUserViewModel.createNewUser(mockedUser);
        assertTrue(isUserCreated[0]);
    }
}
