package smikhlevskiy.uafinance.model;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import smikhlevskiy.uafinance.Utils.UAFConst;

/**
 * Created by tcont98 on 07-Nov-15.
 */
public class FinanceUA {
    static final String TAG = FinanceUA.class.getSimpleName();
    private String sourceId;
    private String date;
    private Organization[] organizations;
    private Map<String, String> orgTypes;
    private Map<String, String> currencies;
    private Map<String, String> regions;

    private Map<String, String> cities;


    private ArrayList<Integer> fu_index = new ArrayList<Integer>();



/*
    public Set<String> getAllCurrencies() {
        Set<String> currArr = new HashSet<String>();
        currArr.add("aaa");


        for (Organization organization : organizations) {


            if (organization.getCurrencies() != null) {
                Set<String> namesCurr = organization.getCurrencies().keySet();
                for (String name : namesCurr) currArr.add(name);
            }


        }


        return currArr;
    }
*/


    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Organization[] getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Organization[] organizations) {
        this.organizations = organizations;
    }

    public Map<String, String> getOrgTypes() {
        return orgTypes;
    }

    public void setOrgTypes(Map<String, String> orgTypes) {
        this.orgTypes = orgTypes;
    }

    public Map<String, String> getCurrancies() {
        return currencies;
    }

    public void setCurrancies(Map<String, String> currancies) {
        this.currencies = currancies;
    }

    public Map<String, String> getRegions() {
        return regions;
    }

    public void setRegions(Map<String, String> regions) {
        this.regions = regions;
    }

    public Map<String, String> getCities() {
        return cities;
    }

    public void setCities(Map<String, String> cities) {
        this.cities = cities;
    }

    public ArrayList<Integer> getFu_index() {
        return fu_index;
    }

    public void sort(boolean askByd, boolean sortCurrency, String city, String currancie, Location deviceLocation) {

        fu_index.clear();

        Location location = new Location("");
        Location bLocation = new Location("");

        for (int i = 0; i < organizations.length; i++) {

            //---------------cack Distance for organiztations-----(sort by Distance)
            if ((!sortCurrency)) {
                if ((deviceLocation != null) && (organizations[i].getLatLong() != null)) {
                    location.setLatitude(organizations[i].getLatLong().latitude);
                    location.setLongitude(organizations[i].getLatLong().longitude);
                    organizations[i].setDistance(location.distanceTo(deviceLocation));

                } else
                    organizations[i].setDistance(Double.MAX_VALUE);

                //  find nearby organization in brunch and move it to root
                if (organizations[i].getOrganizationBrunches() != null) {
                    Organization maxOrg = null;
                    Location bLockation = null;
                    int n = 0;
                    for (Organization bOrganization : organizations[i].getOrganizationBrunches())
                        if ((deviceLocation != null) && (bOrganization.getLatLong() != null)) {
                            bLocation.setLatitude(bOrganization.getLatLong().latitude);
                            bLocation.setLongitude(bOrganization.getLatLong().longitude);
                            bOrganization.setDistance(bLocation.distanceTo(deviceLocation));

                            Log.i(TAG, bOrganization.getAddress() + " " + bLocation.distanceTo(deviceLocation));


                            if ((maxOrg == null) || (maxOrg.getDistance() > bOrganization.getDistance())) {
                                maxOrg = bOrganization;

                            }

                        }


                    //move nearby organization to root&and  old root move to brunch
                    if ((maxOrg != null) && (maxOrg.getDistance() < organizations[i].getDistance())) {
                        Log.i(TAG, "OLD root " + organizations[i].getAddress() + " " + organizations[i].getDistance());
                        Log.i(TAG, "NEW root " + maxOrg.getAddress() + " " + maxOrg.getDistance());

                        organizations[i].getOrganizationBrunches().remove(maxOrg);//remove nearby organization from brunch
                        maxOrg.setOrganizationBrunches(organizations[i].getOrganizationBrunches());//move brunch from old root to nearby organization
                        organizations[i].setOrganizationBrunches(null);//remove brunch from old root
                        maxOrg.getOrganizationBrunches().add(organizations[i]);//add old root to brunch of nearby organization
                        organizations[i] = maxOrg;//set nearby organization to root


                    }
                }


            }


            organizations[i].setCurrentCurrencyVal(0.0);

            if (cities.get(organizations[i].getCityId()).equals(city))
                if (organizations[i].currencies.containsKey(currancie)) {
                    fu_index.add(new Integer(i));
                    if (askByd)
                        organizations[i].setCurrentCurrencyVal(new Double(organizations[i].currencies.get(currancie).getBid()));
                    else
                        organizations[i].setCurrentCurrencyVal(new Double(organizations[i].currencies.get(currancie).getAsk()));
                }
        }


        for (int i = 0; i < fu_index.size() - 1; i++)
            for (int j = 0; j < fu_index.size() - 1; j++) {

                if (sortCurrency) {//Sort by Currancy
                    if ((askByd && (organizations[fu_index.get(j + 1)].getCurrentCurrencyVal() > organizations[fu_index.get(j)].getCurrentCurrencyVal())) ||
                            ((!askByd) && (organizations[fu_index.get(j + 1)].getCurrentCurrencyVal() < organizations[fu_index.get(j)].getCurrentCurrencyVal()))) {
                        Integer a = fu_index.get(j);
                        fu_index.set(j, fu_index.get(j + 1));
                        fu_index.set(j + 1, a);


                    }

                    //   s=organization.currencies.get(uaFinancePreference.getCurrancie()).getAsk();

                } else {//sort by Distance
                    if (organizations[fu_index.get(j + 1)].getDistance() < organizations[fu_index.get(j)].getDistance()) {
                        Integer a = fu_index.get(j);
                        fu_index.set(j, fu_index.get(j + 1));
                        fu_index.set(j + 1, a);


                    }

                    //   s=organization.currencies.get(uaFinancePreference.getCurrancie()).getAsk();

                }

            }

/*
        if (organization.currencies.containsKey(uaFinancePreference.getCurrancie()))
            if (uaFinancePreference.getAskBid().equals(context.getResources().getStringArray(R.array.askbid)[0]))
                s=organization.currencies.get(uaFinancePreference.getCurrancie()).getBid(); else
                s=organization.currencies.get(uaFinancePreference.getCurrancie()).getAsk();
                */


    }

    public HashMap<String, Currencie> calckMinMaxCurrencies(String[] keyCurrencies, String city) {
        HashMap<String, Currencie> minMaxCurrencies = new HashMap<String, Currencie>();

        for (String key : keyCurrencies) {
            if (!minMaxCurrencies.containsKey(key)) {
                Currencie maxcur = new Currencie();
                maxcur.setAsk("0.0");
                maxcur.setBid("0.0");
                minMaxCurrencies.put(key, maxcur);
            }

            for (int i = 0; i < organizations.length; i++) {
                if (organizations[i].currencies.containsKey(key) && (cities.get(organizations[i].getCityId()).equals(city))) {

                    Currencie maxcur = (Currencie) minMaxCurrencies.get(key);
                    Currencie orgCur = organizations[i].currencies.get(key);

                    double maxAskVal = new Double(maxcur.getAsk());
                    double minBidVal = new Double(maxcur.getBid());
                    double askVal = new Double(orgCur.getAsk());
                    double bidVal = new Double(orgCur.getBid());

                    if ((maxAskVal > askVal) || (maxAskVal == 0)) {
                        maxcur.setAsk(Double.toString(askVal));

                    }
                    if ((minBidVal < bidVal) || (minBidVal == 0))
                        maxcur.setBid(Double.toString(bidVal));


                }
            }

        }

        return minMaxCurrencies;
    }


    public static String getAddressbyAdressCity(String city, String address) {
        return ("Украина, " + city + ", " + address).replace(' ', '+');

    }

    public String getURLAddressByOrganization(Organization organization) {
        if (cities.containsKey(organization.getCityId())) {
            String mcity = cities.get(organization.getCityId());

            return getAddressbyAdressCity(mcity, organization.getAddress());


        }
        return "";
    }

    public List<String> getAllAddresses(String prefCity) {
        ArrayList<String> list = new ArrayList<String>();


//-----j==0  -> current City    j==1 other City---
        for (int j = 0; j <= 1; j++) {
            for (Organization organization : organizations) {
                if (cities.containsKey(organization.getCityId())) {
                    String mcity = cities.get(organization.getCityId());
                    if (((j == 0) && mcity.equals(prefCity)) || ((j == 1) && (!mcity.equals(prefCity)))) {
                        list.add(getURLAddressByOrganization(organization));
                        if (organization.getOrganizationBrunches() != null)
                            for (Organization organizationBrunch : organization.getOrganizationBrunches())
                                list.add(getURLAddressByOrganization(organizationBrunch));
                    }
                }
            }
        }
        return list;
    }

    public void optimizeOrganizationList(String city, ArrayList<Organization> privatAdresses) {

        LinkedList<Organization> organizationsList = new LinkedList<Organization>();

        LinkedList<String> organizationsTitleList = new LinkedList<String>();

        Organization privatEtalon = null;

        for (int i = 0; i < organizations.length; i++)
            if (city.equals(cities.get(organizations[i].getCityId()))) {


                organizationsList.add(organizations[i]);
                organizationsTitleList.add(organizations[i].getTitle().toLowerCase());


            }

        //----------------------Add privat addresses from PrivatBank site-----
        if (privatAdresses != null) {
            Organization organizationEtalon = null;
            //--------------remove all Old privat addresses------
            for (int j = 0; j < organizationsList.size(); j++)
                if (organizationsTitleList.get(j).contains(UAFConst.banksLc[UAFConst.PRIVAT_ID])) {
                    organizationEtalon = organizationsList.get(j);

                    organizationsList.remove(organizationsList.get(j));//delete
                    organizationsTitleList.remove(j);
                }

            if (organizationEtalon != null) {
                for (int j = 0; j < privatAdresses.size(); j++) {
                    privatAdresses.get(j).setCityId(organizationEtalon.getCityId());
                    privatAdresses.get(j).setCurrencies(organizationEtalon.getCurrencies());
                    privatAdresses.get(j).setOrgType(organizationEtalon.getOrgType());
                    privatAdresses.get(j).setRegionId(organizationEtalon.getRegionId());
                    organizationsList.add(privatAdresses.get(j));
                    organizationsTitleList.add(privatAdresses.get(j).getTitle().toLowerCase());
                }
            }

        }


        Organization rootOrganization;
        String titleLC;
        for (int j = 0; j < organizationsList.size(); j++) {
            rootOrganization = organizationsList.get(j);

            //-----------detect Bank-----------------
            titleLC =organizationsTitleList.get(j);
            for (int n = 0; n < UAFConst.banksLc.length; n++)
                if (titleLC.contains(UAFConst.banksLc[n]))
                    titleLC = UAFConst.banksLc[n];

            /*-----move brunches of bank  to brunch------*/
            for (int i = organizationsList.size() - 1; i >= j + 1; i--) {
                if (organizationsTitleList.get(i).contains(titleLC)) {
                    Log.i("Optimization", "Remove" + organizationsList.get(i).getTitle());

                    if (rootOrganization.getOrganizationBrunches() == null)
                        rootOrganization.setOrganizationBrunches(new ArrayList<Organization>());

                    //move organization from root to brunch
                    rootOrganization.getOrganizationBrunches().add(organizationsList.get(i));//add
                    organizationsList.remove(organizationsList.get(i));//delete
                    organizationsTitleList.remove(i);

                }

            }

        }


        organizations = new Organization[organizationsList.size()];
        for (int i = 0; i < organizationsList.size(); i++)
            organizations[i] = organizationsList.get(i);


    }


    public FinanceUA() {
        fu_index = new ArrayList<Integer>();
    }

}