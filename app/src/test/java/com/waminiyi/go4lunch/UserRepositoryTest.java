package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Objects;

@RunWith(JUnit4.class)
public class UserRepositoryTest {
    FirebaseUser mockedUser = mock(FirebaseUser.class);
    FirebaseHelper mockedHelper = mock(FirebaseHelper.class);
    UserRepository userRepo = new UserRepository(mockedHelper);
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

        when(mockedHelper.getCurrentUser()).thenReturn(mockedUser);
        when(mockedHelper.getCurrentUserUID()).thenReturn(id);
        when(mockedUserDoc.toObject(UserEntity.class)).thenReturn(user);
    }


    /**
     * Test that the getCurrentUser method return the current connected FirebaseUser
     */
    @Test
    public void getCurrentUserTest() {
        assertEquals(mockedUser, userRepo.getCurrentUser());
        assertEquals(id, userRepo.getCurrentUser().getUid());
        assertEquals(name, userRepo.getCurrentUser().getDisplayName());
        assertEquals(mail, userRepo.getCurrentUser().getEmail());
    }


    /**
     * Test that the parseCurrentUserDoc method turn passed DocumentSnapshot to UserEntity and
     * that this is returned by the getCurrentUserData method
     */
    @Test
    public void getCurrentUserDataTest() {

        userRepo.parseCurrentUserDoc(mockedUserDoc);
        try {
            Thread.sleep(2000); //waiting for the result to be post in the livedata
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        UserEntity retrievedUserEntity = userRepo.getCurrentUserData().getValue();

        assertEquals(id, Objects.requireNonNull(retrievedUserEntity).getUId());
        assertEquals(name, Objects.requireNonNull(retrievedUserEntity).getUserName());
        assertEquals(mail, Objects.requireNonNull(retrievedUserEntity).getUserEmail());
        assertEquals(photoUrl, Objects.requireNonNull(retrievedUserEntity).getPhotoUrl());
    }

    /**
     * Verify that the logOut method call the FirebaseHelper logOut method
     */
    @Test
    public void logoutTest() {
        userRepo.logOut();
        verify(mockedHelper).logOut();
    }


    /**
     * Verify that the createNewUserInDatabase method in UserRepository calls the FirebaseHelper
     * createNewUserInDatabase method
     */
    @Test
    public void createUserTest() {
        userRepo.createNewUserInDatabase(mockedUser);
        verify(mockedHelper).createNewUserInDatabase(mockedUser);
    }


    /**
     * Verify that the isCurrentUserLogged method return whether the current FirebaseUser is null or
     * not
     */
    @Test
    public void isUserLoggedTest() {
        assertTrue(userRepo.isCurrentUserLogged());
    }


    /**
     * Verify that the getCurrentUserUID method return the current FirebaseUser ID
     */
    @Test
    public void getCurrentUserUIDTest() {
        assertEquals(id, userRepo.getCurrentUserUID());
    }


    /**
     * Verify that the addRestaurantToUserFavorite method of UserRepository calls
     * addRestaurantToUserFavorite of the FirebaseHelper
     */
    @Test
    public void addRestaurantToUserFavoriteTest() {
        String restaurantId = "RESTAURANT_ID";
        userRepo.addRestaurantToUserFavorite(restaurantId);
        verify(mockedHelper).addRestaurantToUserFavorite(restaurantId);
    }

    /**
     * Verify that the removeRestaurantFromUserFavorite method of UserRepository calls
     * removeRestaurantFromUserFavorite of the FirebaseHelper
     */
    @Test
    public void removeRestaurantFromUserFavoriteTest() {
        String restaurantId = "RESTAURANT_ID";
        userRepo.removeRestaurantFromUserFavorite(restaurantId);
        verify(mockedHelper).removeRestaurantFromUserFavorite(restaurantId);
    }


    /**
     * Verify that the updateProfile method of UserRepository calls updateProfile
     * of the FirebaseHelper
     */
    @Test
    public void updateProfileTest() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().build();

        userRepo.updateProfile(profileUpdates);
        verify(mockedHelper).updateProfile(profileUpdates);
    }

    /**
     * Verify that the updateUserName method of UserRepository calls updateUserName
     * of the FirebaseHelper
     */
    @Test
    public void updateUserNameTest() {
        String newName = "This Is OO7";
        userRepo.updateUserName(newName);
        verify(mockedHelper).updateUserName(newName);
    }

    /**
     * Verify that the updateUserPic method of UserRepository calls updateUserPic
     * of the FirebaseHelper
     */
    @Test
    public void updateUserPicTest() {
        String newPic = "https://www.007.com/wp-content/uploads/2022/11/LS_Being_Bond_4.jpg";
        userRepo.updateUserPic(newPic);
        verify(mockedHelper).updateUserPic(newPic);
    }

}
