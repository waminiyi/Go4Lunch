package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.repository.UserRepository;

import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.Objects;

public class UserRepositoryTest {
    FirebaseUser mockedUser = mock(FirebaseUser.class);
    UserRepository mockedUserRepo = mock(UserRepository.class);
    UserEntity user = new UserEntity("userId", "userName", "useMmail", "userPhone", "urlPicture");

    @Test
    public void getCurrentUserTest() {
        when(mockedUserRepo.getCurrentUser()).thenReturn(mockedUser);
        assertEquals(mockedUser.getUid(), mockedUserRepo.getCurrentUser().getUid());
    }

    @Test
    public void getCurrentUserDataTest() {
        when(mockedUserRepo.getCurrentUserData()).thenReturn(new MutableLiveData<>(user));

        assertEquals("userId", Objects.requireNonNull(mockedUserRepo.getCurrentUserData().getValue()).getuId());
    }

    @Test
    public void logoutTest() {

        final boolean[] isUserLogged = {true};

        doAnswer((Answer<Void>) invocation -> {
            isUserLogged[0] = false;
            return null;
        }).when(mockedUserRepo).logOut();

        mockedUserRepo.logOut();
        assertFalse(isUserLogged[0]);
    }



    @Test
    public void createUserTest() {
        final boolean[] isUserCreated = {false};

        doAnswer((Answer<Void>) invocation -> {
            isUserCreated[0] = true;
            return null;
        }).when(mockedUserRepo).createNewUser(mockedUser);

        mockedUserRepo.createNewUser(mockedUser);
        assertTrue(isUserCreated[0]);
    }
}
