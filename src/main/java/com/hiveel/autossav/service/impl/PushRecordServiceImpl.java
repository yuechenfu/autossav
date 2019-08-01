package com.hiveel.autossav.service.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hiveel.autossav.dao.*;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.PushRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PushRecordServiceImpl implements PushRecordService {
    @Autowired
    private PushRecordDao dao;
    @Autowired
    private VehicleDao vehicleDao;
    @Autowired
    private InspectionDao inspectionDao;
    @Autowired
    private IssueDao issueDao;
    @Autowired
    private VehicleDriverRelateDao vehicleDriverRelateDao;
    @Autowired
    private ProblemDao problemDao;
    @Autowired
    private PersonDao personDao;
    @Autowired
    private ExamDao examDao;
    @Autowired
    private PlanDao planDao;

    @Override
    public int save(PushRecord e) {
        e.fillNotRequire();
        e.createAt();
        e.updateAt();
        setPayload2Data(e);
        return dao.save(e);
    }

    private void setPayload2Data(PushRecord e) {
        Map<String, Object> payLoad = e.getPayLoad();
        if (payLoad == null || payLoad.isEmpty()) {
            return;
        }
        List<PushRecord.PayLoadUnit> payLoadUnitList = new ArrayList<>();
        payLoad.keySet().stream().forEach(key -> {
            Object o = payLoad.get(key);
            PushRecord.PayLoadUnit payLoadUnit = new PushRecord.PayLoadUnit();
            String className = o.getClass().getSimpleName();
            String value = o.toString();
            if (o instanceof Issue) {
                value = ((Issue) o).getId().toString();
            } else if (o instanceof Inspection) {
                value = ((Inspection) o).getId().toString();
            } else if (o instanceof Plan) {
                value = ((Plan) o).getId().toString();
            } else if (o instanceof Vehicle) {
                value = ((Vehicle) o).getId().toString();
            } else if (o instanceof VehicleDriverRelate) {
                value = ((VehicleDriverRelate) o).getId().toString();
            } else if (o instanceof Problem) {
                value = ((Problem) o).getId().toString();
            } else if (o instanceof Person) {
                value = ((Person) o).getId().toString();
            } else if (o instanceof Exam) {
                value = ((Exam) o).getId().toString();
            } else if (o instanceof String) {
                value = String.valueOf(o);
            }
            payLoadUnit.setClassName(className);
            payLoadUnit.setKey(key);
            payLoadUnit.setValue(value);
            payLoadUnitList.add(payLoadUnit);
        });
        String data = new Gson().toJson(payLoadUnitList);
        e.setData(data);
    }

    private void findPayloadFromData(PushRecord e) {
        String data = e.getData();
        if (data == null) {
            return;
        }
        Map<String, Object> payLoad = new HashMap<>();
        List<PushRecord.PayLoadUnit> payLoadUnitList = new GsonBuilder().create().fromJson(data, new TypeToken<List<PushRecord.PayLoadUnit>>() {
        }.getType());
        payLoadUnitList.stream().forEach(payLoadUnit -> {
            String className = payLoadUnit.getClassName();
            String key = payLoadUnit.getKey();
            String value = payLoadUnit.getValue();
            Object object = null;
            if (className.equals("Issue")) {
                object = issueDao.findById(new Issue.Builder().set("id", Long.valueOf(value)).build());
                //todo 临时满足shaun的需求，后续删除
                e.setIssue((Issue)object);
            } else if (className.equals("Inspection")) {
                object = inspectionDao.findById(new Inspection.Builder().set("id", Long.valueOf(value)).build());
                //todo 临时满足shaun的需求，后续删除
                e.setInspection((Inspection) object);
            } else if (className.equals("Plan")) {
                object = planDao.findById(new Plan.Builder().set("id", Long.valueOf(value)).build());
            } else if (className.equals("Vehicle")) {
                object = vehicleDao.findById(new Vehicle.Builder().set("id", Long.valueOf(value)).build());
                //todo 临时满足shaun的需求，后续删除
                e.setVehicle((Vehicle)object);
            } else if (className.equals("VehicleDriverRelate")) {
                object = vehicleDriverRelateDao.findById(new VehicleDriverRelate.Builder().set("id", Long.valueOf(value)).build());
            } else if (className.equals("Problem")) {
                object = problemDao.findById(new Problem.Builder().set("id", Long.valueOf(value)).build());
            } else if (className.equals("Person")) {
                object = personDao.findById(new Person.Builder().set("id", Long.valueOf(value)).build());
            } else if (className.equals("Exam")) {
                object = examDao.findById(new Exam.Builder().set("id", Long.valueOf(value)).build());
            } else  {
                object = value;
            }
            payLoad.put(key, object);
        });
        e.setPayLoad(payLoad);
    }


    @Override
    public boolean update(PushRecord e) {
        e.updateAt();
        return dao.update(e) == 1;
    }

    @Override
    public boolean delete(PushRecord e) {
        return dao.delete(e) == 1;
    }

    @Override
    public PushRecord findById(PushRecord e) {
        PushRecord result = dao.findById(e);
        findPayloadFromData(result);
        return result != null ? result : PushRecord.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<PushRecord> find(SearchCondition searchCondition) {
        searchCondition.setDefaultSortBy("updateAt", true);
        List<PushRecord> list =  dao.find(searchCondition);
        list.stream().forEach(inDb-> findPayloadFromData(inDb));
        return list;
    }

    @Override
    public int countByPerson(SearchCondition searchCondition, PushRecord e) {
        return dao.countByPerson(searchCondition, e);
    }

    @Override
    public List<PushRecord> findByPerson(SearchCondition searchCondition, PushRecord e) {
        searchCondition.setDefaultSortBy("updateAt", true);
        List<PushRecord> list = dao.findByPerson(searchCondition, e);
        list.stream().forEach(inDb -> {
            findPayloadFromData(inDb);
            inDb.setContent(PushRecordType.getContent(inDb));
        });
        return list;
    }


}
