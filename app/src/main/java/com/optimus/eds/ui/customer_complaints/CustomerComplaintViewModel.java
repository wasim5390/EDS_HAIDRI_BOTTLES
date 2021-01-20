package com.optimus.eds.ui.customer_complaints;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.optimus.eds.ui.customer_complaints.model.ComplaintReasonModel;
import com.optimus.eds.ui.customer_complaints.model.ComplaintTypeModel;

import java.util.Arrays;
import java.util.List;

/**
 * Created By apple on 5/2/19
 */
public class CustomerComplaintViewModel extends AndroidViewModel {

    private MutableLiveData<List<ComplaintTypeModel>> complaintsData;

    public CustomerComplaintViewModel(@NonNull Application application) {
        super(application);
        complaintsData = new MutableLiveData<>();
    }

    public void setComplaintsData(){
        complaintsData.setValue(makeMultiCheckGenres());
    }


    public MutableLiveData<List<ComplaintTypeModel>> getComplaintsData() {
        return complaintsData;
    }

    private List<ComplaintTypeModel> makeMultiCheckGenres() {
        return Arrays.asList(makeMultiCheckRockGenre()
                ,makeMultiCheckJazzGenre()
                ,makeMultiCheckClassicGenre()
        );
    }

    private ComplaintTypeModel makeMultiCheckRockGenre() {
        return new ComplaintTypeModel("Rock", makeRockArtists());
    }


    private ComplaintTypeModel makeMultiCheckJazzGenre() {
        return new ComplaintTypeModel("Jazz", makeJazzArtists());
    }

    private ComplaintTypeModel makeMultiCheckClassicGenre() {
        return new ComplaintTypeModel("Classic", makeClassicArtists());
    }


    private List<ComplaintReasonModel> makeRockArtists() {
        ComplaintReasonModel queen = new ComplaintReasonModel("Queen", false);
        ComplaintReasonModel styx = new ComplaintReasonModel("Styx", false);
        ComplaintReasonModel reoSpeedwagon = new ComplaintReasonModel("REO Speedwagon", false);
        ComplaintReasonModel boston = new ComplaintReasonModel("Boston", false);

        return Arrays.asList(queen, styx, reoSpeedwagon, boston);
    }

    private List<ComplaintReasonModel> makeJazzArtists() {
        ComplaintReasonModel milesDavis = new ComplaintReasonModel("Miles Davis", false);
        ComplaintReasonModel ellaFitzgerald = new ComplaintReasonModel("Ella Fitzgerald", false);
        ComplaintReasonModel billieHoliday = new ComplaintReasonModel("Billie Holiday", false);

        return Arrays.asList(milesDavis, ellaFitzgerald, billieHoliday);
    }


    private List<ComplaintReasonModel> makeClassicArtists() {
        ComplaintReasonModel beethoven = new ComplaintReasonModel("Ludwig van Beethoven", false);
        ComplaintReasonModel bach = new ComplaintReasonModel("Johann Sebastian Bach", false);
        ComplaintReasonModel brahms = new ComplaintReasonModel("Johannes Brahms", false);
        ComplaintReasonModel puccini = new ComplaintReasonModel("Giacomo Puccini", false);

        return Arrays.asList(beethoven, bach, brahms, puccini);
    }
}
