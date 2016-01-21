package smikhlevskiy.uafinance.model;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import smikhlevskiy.uafinance.Utils.UAFConstansts;

/**
 * Created by tcont98 on 07-Nov-15.
 */
public class FinanceUA {

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

        for (int i = 0; i < organizations.length; i++) {
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

                if (sortCurrency) {
                    if ((askByd && (organizations[fu_index.get(j + 1)].getCurrentCurrencyVal() > organizations[fu_index.get(j)].getCurrentCurrencyVal())) ||
                            ((!askByd) && (organizations[fu_index.get(j + 1)].getCurrentCurrencyVal() < organizations[fu_index.get(j)].getCurrentCurrencyVal()))) {
                        Integer a = fu_index.get(j);
                        fu_index.set(j, fu_index.get(j + 1));
                        fu_index.set(j + 1, a);


                    }

                    //   s=organization.currencies.get(uaFinancePreference.getCurrancie()).getAsk();

                } else
                 {//sort Distance
                    if ((askByd && (organizations[fu_index.get(j + 1)].getCurrentCurrencyVal() > organizations[fu_index.get(j)].getCurrentCurrencyVal())) ||
                            ((!askByd) && (organizations[fu_index.get(j + 1)].getCurrentCurrencyVal() < organizations[fu_index.get(j)].getCurrentCurrencyVal()))) {
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

    public String AddressByOrganization(Organization organization) {
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
                        list.add(AddressByOrganization(organization));
                        if (organization.getOrganizationBrunches() != null)
                            for (Organization organizationBrunch : organization.getOrganizationBrunches())
                                list.add(AddressByOrganization(organizationBrunch));
                    }
                }
            }
        }
        return list;
    }

    public void optimizeOrganizationList(String city) {

        LinkedList<Organization> organizationsList = new LinkedList<Organization>();

        LinkedList<String> organizationsTitleList = new LinkedList<String>();


        for (int i = 0; i < organizations.length; i++)
            if (city.equals(cities.get(organizations[i].getCityId()))) {

                organizationsList.add(organizations[i]);
                organizationsTitleList.add(organizations[i].getTitle().toLowerCase());


            }

        Organization rootOrganization;
        for (int j = 0; j < organizationsList.size(); j++) {
            rootOrganization = organizationsList.get(j);

            if (organizationsTitleList.get(j).contains(UAFConstansts.UKRSOC_LC) &&
                    (!organizationsTitleList.get(j).equals(UAFConstansts.UKRSOC_LC))) {
                organizationsTitleList.set(j, UAFConstansts.UKRSOC_LC);
                rootOrganization.setTitle(UAFConstansts.UKRSOC);
            }

            if (organizationsTitleList.get(j).contains(UAFConstansts.PRIVAT_LC) &&
                    (!organizationsTitleList.get(j).equals(UAFConstansts.PRIVAT_LC))) {
                organizationsTitleList.set(j, UAFConstansts.PRIVAT_LC);
                rootOrganization.setTitle(UAFConstansts.PRIVAT);
            }

            for (int i = organizationsList.size() - 1; i >= j + 1; i--) {

                if (
                        (organizationsTitleList.get(i).contains(organizationsTitleList.get(j))) &&
                                /*organizationsList.get(i).getCityId().equals(rootOrganization.getCityId()) &&*/
                                (!organizationsList.get(i).getTitle().equalsIgnoreCase(rootOrganization.getTitle()))


                        ) {
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