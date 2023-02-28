package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.repository.UserRepository;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Objects;

public class UserViewModelTest {

    FirebaseUser mockedUser = mock(FirebaseUser.class);
    UserRepository mockedUserRepo = mock(UserRepository.class);
    UserViewModel userVM = new UserViewModel(mockedUserRepo);
    DocumentSnapshot mockedUserDoc = mock(DocumentSnapshot.class);

    private final String id = "JAMESBOND007";
    private final String name = "James Bond";
    private final String mail = "jamesbond@james.bond";
    private final String photoUrl =
            "https://www.007.com/wp-content/uploads/2022/11/LS_Being_Bond_1.jpg";
    UserEntity user = new UserEntity(id, name, mail, "userPhone", photoUrl);

    @Rule
    public InstantTaskExecutorRule taskRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        when(mockedUser.getDisplayName()).thenReturn(name);
        when(mockedUser.getEmail()).thenReturn(mail);
        when(mockedUser.getPhotoUrl()).thenReturn(Uri.parse(photoUrl));
        when(mockedUser.getUid()).thenReturn(id);

        when(mockedUserRepo.getCurrentUser()).thenReturn(mockedUser);
        when(mockedUserRepo.getCurrentUserUID()).thenReturn(id);
        when(mockedUserRepo.isCurrentUserLogged()).thenReturn(mockedUser != null);
        when(mockedUserRepo.getCurrentUserData()).thenReturn(new MutableLiveData<>(user));
        when(mockedUserDoc.toObject(UserEntity.class)).thenReturn(user);
    }


    /**
     * Test that the getCurrentUser method return the result of getCurrentUser from UserRepository
     */
    @Test
    public void getCurrentUserTest() {
        assertEquals(mockedUser, userVM.getCurrentUser());
        assertEquals(id, userVM.getCurrentUser().getUid());
        assertEquals(name, userVM.getCurrentUser().getDisplayName());
        assertEquals(mail, userVM.getCurrentUser().getEmail());
    }


    /**
     * Test that the parseCurrentUserDoc method calls parseCurrentUserDoc of UserRepository and
     * that the getCurrentUserData method returns the result of getCurrentUserData from
     * UserRepository
     */
    @Test
    public void getCurrentUserDataTest() {

        userVM.parseCurrentUserDoc(mockedUserDoc);
        UserEntity retrievedUserEntity = userVM.getCurrentUserData().getValue();

        verify(mockedUserRepo).parseCurrentUserDoc(mockedUserDoc);
        verify(mockedUserRepo).getCurrentUserData();

        assertEquals(id, Objects.requireNonNull(retrievedUserEntity).getUId());
        assertEquals(name, Objects.requireNonNull(retrievedUserEntity).getUserName());
        assertEquals(mail, Objects.requireNonNull(retrievedUserEntity).getUserEmail());
        assertEquals(photoUrl, Objects.requireNonNull(retrievedUserEntity).getPhotoUrl());
    }

    /**
     * Verify that the logOut method calls the UserRepository logOut method
     */
    @Test
    public void logoutTest() {
        userVM.logOut();
        verify(mockedUserRepo).logOut();
    }


    /**
     * Verify that the createNewUserInDatabase method in UserViewModel  call the UserRepository
     * createNewUserInDatabase method
     */
    @Test
    public void createUserTest() {
        userVM.createNewUserInDatabase(mockedUser);
        verify(mockedUserRepo).createNewUserInDatabase(mockedUser);
    }


    /**
     * Verify that the isCurrentUserLogged method in UserViewModel returns the result of
     * isCurrentUserLogged from UserRepository
     */
    @Test
    public void isUserLoggedTest() {
        assertTrue(userVM.isCurrentUserLogged());
    }

    /**
     * Verify that the getCurrentUserUID method in UserViewModel returns the result of
     * getCurrentUserUID from UserRepository
     */
    @Test
    public void getCurrentUserUIDTest() {
        assertEquals(id, userVM.getCurrentUserUID());
    }


    /**
     * Verify that the addRestaurantToUserFavorite method of UserViewModel calls
     * addRestaurantToUserFavorite method of the UserRepository
     */
    @Test
    public void addRestaurantToUserFavoriteTest() {
        String restaurantId = "RESTAURANT_ID";
        userVM.addRestaurantToUserFavorite(restaurantId);
        verify(mockedUserRepo).addRestaurantToUserFavorite(restaurantId);
    }

    /**
     * Verify that the removeRestaurantFromUserFavorite method of UserViewModel calls
     * removeRestaurantFromUserFavorite method of the UserRepository
     */
    @Test
    public void removeRestaurantFromUserFavoriteTest() {
        String restaurantId = "RESTAURANT_ID";
        userVM.removeRestaurantFromUserFavorite(restaurantId);
        verify(mockedUserRepo).removeRestaurantFromUserFavorite(restaurantId);
    }


    /**
     * Verify that the updateProfile method of UserViewModel calls updateProfile method
     * of the UserRepository
     */
    @Test
    public void updateProfileTest() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().build();

        userVM.updateProfile(profileUpdates);
        verify(mockedUserRepo).updateProfile(profileUpdates);
    }

    /**
     * Verify that the updateUserName method of UserViewModel calls updateUserName method
     * of the UserRepository
     */
    @Test
    public void updateUserNameTest() {
        String newName = "This Is OO7";
        userVM.updateUserName(newName);
        verify(mockedUserRepo).updateUserName(newName);
    }

    /**
     * Verify that the updateUserPic method of UserViewModel calls updateUserPic method
     * of the UserRepository
     */
    @Test
    public void updateUserPicTest() {
        String newPic = "https://www.007.com/wp-content/uploads/2022/11/LS_Being_Bond_4.jpg";
        userVM.updateUserPic(newPic);
        verify(mockedUserRepo).updateUserPic(newPic);
    }
}
