package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.model.UserLunch;
import com.waminiyi.go4lunch.repository.LunchRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RunWith(JUnit4.class)
public class LunchRepositoryTest {

    FirebaseHelper mockedHelper = mock(FirebaseHelper.class);
    LunchRepository lunchRepo = new LunchRepository(mockedHelper);
    private final String restaurantId = "rid", restaurantId2 = "rid2";
    private final String id = "uid", id2 = "uid2";
    User user = new User(id, "james", "https://james.com");
    User user2 = new User(id2, "bond", "https://bond.com");

    Lunch lunch = new Lunch(id, restaurantId, "Good food");
    Lunch lunch2 = new Lunch(id2, restaurantId2, "Top food");
    DocumentSnapshot mockedUserSnippetDoc = mock(DocumentSnapshot.class);
    Map<String, Object> userData = new HashMap<>();
    DocumentSnapshot mockedLunchDoc = mock(DocumentSnapshot.class);
    Task<DocumentSnapshot> lunchTask = mock(Task.class);

    @Rule
    public InstantTaskExecutorRule taskRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        userData.put(id, user);
        userData.put(id2, user2);

        when(lunchTask.addOnSuccessListener(documentSnapshot -> {
        })).thenReturn(lunchTask);
        when(lunchTask.getResult()).thenReturn(mockedLunchDoc);

        when(mockedHelper.getLunches()).thenReturn(lunchTask);
        when(mockedUserSnippetDoc.getData()).thenReturn(userData);
        when(mockedUserSnippetDoc.get(id, User.class)).thenReturn(user);
        when(mockedUserSnippetDoc.get(id2, User.class)).thenReturn(user2);
        when(mockedHelper.getCurrentUserUID()).thenReturn(id);
        when(mockedLunchDoc.get(id, Lunch.class)).thenReturn(lunch);
        when(mockedLunchDoc.get(id2, Lunch.class)).thenReturn(lunch2);
    }


    /**
     * Verify that the  setCurrentUserLunch method of LunchRepository calls the FirebaseHelper
     * setCurrentUserLunch method
     */
    @Test
    public void setCurrentUserLunchTest() {
        lunchRepo.setCurrentUserLunch(lunch);
        verify(mockedHelper).setCurrentUserLunch(lunch);
    }


    /**
     * Verify that the  deleteCurrentUserLunch method of LunchRepository calls the FirebaseHelper
     * deleteCurrentUserLunch method
     */
    @Test
    public void deleteCurrentUserLunchTest() {
        lunchRepo.deleteCurrentUserLunch(lunch);
        verify(mockedHelper).deleteCurrentUserLunch(lunch);
    }

    /**
     * Verify that the  parseUsersSnippetDoc method of LunchRepository parse the document into
     * UserLunch and calls the FirebaseHelper getLunches method
     */

    @Test
    public void parseUsersSnippetDocTest() {
        lunchRepo.parseUsersSnippetDoc(mockedUserSnippetDoc);

        List<UserLunch> lunches = lunchRepo.getAllUsersLunches().getValue();

        verify(mockedHelper).getLunches();
        assertEquals(id, Objects.requireNonNull(lunches).get(0).getUserId());
        assertEquals(id2, lunches.get(1).getUserId());
        assertNull(lunches.get(1).getRestaurantId());
    }


    /**
     * Verify that the  parseLunchesDoc method of LunchRepository parses the document and add
     * its data to UserLunch objects
     * UserLunch and calls the FirebaseHelper getLunches method
     */

    @Test
    public void parseLunchesDocTest() {
        lunchRepo.parseUsersSnippetDoc(mockedUserSnippetDoc);
        lunchRepo.parseLunchesDoc(mockedLunchDoc);

        List<UserLunch> lunches = lunchRepo.getAllUsersLunches().getValue();

        assertEquals(lunch.getRestaurantId(), Objects.requireNonNull(lunches).get(0).getRestaurantId());
        assertEquals(lunch2.getUserId(), lunches.get(1).getUserId());
    }

    /**
     * Verify that the  getCurrentUserLunch method of LunchRepository  return the current User's
     * UserLunch
     */

    @Test
    public void getCurrentUserLunchTest() {
        lunchRepo.parseUsersSnippetDoc(mockedUserSnippetDoc);
        lunchRepo.parseLunchesDoc(mockedLunchDoc);

        Lunch uLunch = lunchRepo.getCurrentUserLunch().getValue();

        assertEquals(mockedHelper.getCurrentUserUID(),
                Objects.requireNonNull(uLunch).getUserId());
    }

    /**
     * Verify that the getAllUsersLunches method of LunchRepository  return the list of UserLunch
     */

    @Test
    public void getAllUsersLunchesTest() {
        lunchRepo.parseUsersSnippetDoc(mockedUserSnippetDoc);
        assertEquals(2, Objects.requireNonNull(lunchRepo.getAllUsersLunches().getValue()).size());
    }


    /**
     * Verify that the getCurrentRestaurantLunchesFromDb method of LunchRepository  retrieve the
     * list of User associated with the target restaurant and that this list is returned by
     * the getCurrentRestaurantLunches method
     */

    @Test
    public void getCurrentRestaurantLunchesFromDbTest() {
        lunchRepo.parseUsersSnippetDoc(mockedUserSnippetDoc);
        lunchRepo.parseLunchesDoc(mockedLunchDoc);
        List<User> usersList = lunchRepo.getCurrentRestaurantLunches().getValue();

        assertNull(usersList);

        lunchRepo.getCurrentRestaurantLunchesFromDb(restaurantId);
        usersList = lunchRepo.getCurrentRestaurantLunches().getValue();

        assertEquals(restaurantId, lunch.getRestaurantId());
        assertEquals(lunch.getUserId(),usersList.get(0).getuId());

    }


}
