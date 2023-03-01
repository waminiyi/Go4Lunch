package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.model.UserLunch;
import com.waminiyi.go4lunch.repository.LunchRepository;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Objects;

@RunWith(JUnit4.class)
public class LunchViewModelTest {

    LunchRepository mockedLunchRepo = mock(LunchRepository.class);
    LunchViewModel lunchVM = new LunchViewModel(mockedLunchRepo);
    private final String restaurantId = "rid", restaurantId2 = "rid2";
    private final String id = "uid", id2 = "uid2";
    User user = new User(id, "James", "https://james.com");
    User user2 = new User(id2, "Bond", "https://bond.com");
    UserLunch uLunch = new UserLunch(id, "James", "url.com", restaurantId, "Good food");
    UserLunch uLunch2 = new UserLunch(id2, "Bond", "url.com", restaurantId2, "Top food");

    Lunch lunch = new Lunch(id, restaurantId, "Good food");
    DocumentSnapshot mockedUserSnippetDoc = mock(DocumentSnapshot.class);
    DocumentSnapshot mockedLunchDoc = mock(DocumentSnapshot.class);

    @Rule
    public InstantTaskExecutorRule taskRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        when(mockedLunchRepo.getCurrentUserLunch()).thenReturn(new MutableLiveData<>(lunch));
        when(mockedLunchRepo.getAllUsersLunches()).thenReturn(new MutableLiveData<>(Arrays.asList(uLunch, uLunch2)));

        when(mockedLunchRepo.getCurrentRestaurantLunches()).thenReturn(new MutableLiveData<>(Arrays.asList(user, user2)));
    }


    /**
     * Verify that the setCurrentUserLunch method of LunchViewModel calls the LunchRepository
     * setCurrentUserLunch method
     */
    @Test
    public void setCurrentUserLunchTest() {
        lunchVM.setCurrentUserLunch(lunch);
        verify(mockedLunchRepo).setCurrentUserLunch(lunch);
    }


    /**
     * Verify that the  deleteCurrentUserLunch method of LunchViewModel calls the LunchRepository
     * deleteCurrentUserLunch method
     */
    @Test
    public void deleteCurrentUserLunchTest() {
        lunchVM.deleteCurrentUserLunch(lunch);
        verify(mockedLunchRepo).deleteCurrentUserLunch(lunch);
    }


    /**
     * Verify that the  parseUsersSnippetDoc method of LunchViewModel calls the LunchRepository
     * parseUsersSnippetDoc method
     */

    @Test
    public void parseUsersSnippetDocTest() {
        lunchVM.parseUsersSnippetDoc(mockedUserSnippetDoc);
        verify(mockedLunchRepo).parseUsersSnippetDoc(mockedUserSnippetDoc);
    }


    /**
     * Verify that the  parseLunchesDoc method of LunchViewModel calls the LunchRepository
     * parseLunchesDoc method
     */

    @Test
    public void parseLunchesDocTest() {
        lunchVM.parseLunchesDoc(mockedLunchDoc);
        verify(mockedLunchRepo).parseLunchesDoc(mockedLunchDoc);
    }


    /**
     * Verify that the  getCurrentUserLunch method of LunchViewModel  returns the result of
     * LunchRepository's getCurrentUserLunch method
     */

    @Test
    public void getCurrentUserLunchTest() {

        assertEquals(Objects.requireNonNull(mockedLunchRepo.getCurrentUserLunch().getValue()).getUserId(),
                Objects.requireNonNull(lunchVM.getCurrentUserLunch().getValue()).getUserId());
    }

    /**
     * Verify that the getAllUsersLunches method of LunchViewModel  returns the result of
     * LunchRepository's getAllUsersLunches method
     */

    @Test
    public void getAllUsersLunchesTest() {
        assertEquals(2, Objects.requireNonNull(lunchVM.getAllUsersLunches().getValue()).size());
        assertEquals(Objects.requireNonNull(mockedLunchRepo.getAllUsersLunches().getValue()).get(0).getRestaurantId(),
                lunchVM.getAllUsersLunches().getValue().get(0).getRestaurantId());
    }


    /**
     * Verify that the getCurrentRestaurantLunchesFromDb method of LunchViewModel  call the
     * the getCurrentRestaurantLunchesFromDb method from LunchRepository
     */

    @Test
    public void getCurrentRestaurantLunchesFromDbTest() {
        lunchVM.getCurrentRestaurantLunchesFromDb(restaurantId);
        verify(mockedLunchRepo).getCurrentRestaurantLunchesFromDb(restaurantId);
    }


    /**
     * Verify that the getCurrentRestaurantLunches method of LunchViewModel  returns the result of
     * LunchRepository's getCurrentRestaurantLunches method
     */

    @Test
    public void getCurrentRestaurantLunchesTest() {
        assertEquals(2, Objects.requireNonNull(lunchVM.getCurrentRestaurantLunches().getValue()).size());
        assertEquals(Objects.requireNonNull(mockedLunchRepo.getCurrentRestaurantLunches().getValue()).get(1).getuId(),
                lunchVM.getCurrentRestaurantLunches().getValue().get(1).getuId());
    }
}
