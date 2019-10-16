package com.cefetcontagem.queroferias;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConversorEndCoord {

    private boolean teste = false;
    private Geocoder geocoder;
    private List<Address> list;
    private String error = "", resultAddress = "", resultAddress2 = "";


    public Address coordenadaParaEndereco(double latitude, double longitude){
        resultAddress = "";
        resultAddress2 = "";
        Address aux = null;
        Location location = new Location("oi");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        try{
            list = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            aux = list.get(0);
            for(int i=0, tam= aux.getMaxAddressLineIndex(); i<tam; i++){
                resultAddress += aux.getAddressLine(i);
                resultAddress += i < tam - 1 ? ", " : "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
            error = "Illegal arguments";
        }
        return aux;
    }

    public Double[] enderecoParaCoordenadas(String address){
        teste = true;
        Double[] array = new Double[2];
        resultAddress = "";
        resultAddress2 = "";
        do{
            try {
                list = (ArrayList<Address>) geocoder.getFromLocationName(address, 1);
                teste = false;
            } catch (IOException e) {
                teste = true;
                e.printStackTrace();
                error = "Network problem";
            } catch (IllegalArgumentException e) {
                teste = true;
                e.printStackTrace();
                error = "Illegal arguments";
            }
            if (list == null || !(list.size() > 0)) {
                teste = true;
            }
        }while(teste);
        if(list != null && list.size()>0){
            Address aux = list.get(0);
            resultAddress+= aux.getLatitude();
            resultAddress2+= aux.getLongitude();
        }else{
            resultAddress = error;
        }

        array[0] = Double.parseDouble(resultAddress);
        array[1] = Double.parseDouble(resultAddress2);

        return array;
    }
}
